(ns bugle-forms.model.form
  (:refer-clojure :exclude [get])
  (:require
   [bugle-forms.db.connection :as db]
   [bugle-forms.specs :as specs]
   [clojure.spec.alpha :as s]
   [next.jdbc :as jdbc]
   [next.jdbc.date-time]
   [next.jdbc.sql :as sql]
   [next.jdbc.types :as jdbc-types])
  (:import
   (java.util UUID)
   (java.time Instant)))

(def form-table :form)

(defn create
  "Make a form object suitable to be inserted in database.
  Takes user session data to set the owner of the form."
  [name user-id]
  {:form/id (UUID/randomUUID)
   :form/name name
   :form/owner user-id
   :form/status :draft
   :form/created (Instant/now)})

(defn insert!
  "Insert form in database."
  [form]
  (->> (update form :form/status (comp jdbc-types/as-other name))
       (sql/insert! db/datasource form-table)))

(defn get
  "Retrieve form details."
  [id]
  (if-some [form (some-> (sql/get-by-id db/datasource form-table id)
                         (update :form/status keyword))]
    form
    {:error :form-not-found}))

(defn link
  "Get the share link for a form.
  Returns `nil` if the form is invalid or unpublished."
  [form]
  (when (and (s/valid? ::specs/form form)
             (= :published (:form/status form)))
    (str "/form/" (:form/id form))))

(defn get-forms
  "Retrieve forms belonging to a user."
  [{:keys [uuid]}]
  (into []
        (map (fn [row]
               (-> (select-keys row [:form/id :form/name :form/owner
                                     :form/status :form/created])
                   (update :form/status keyword))))
        (jdbc/plan db/datasource
                   ["select id, name, owner, status, created
                    from form where owner = ?
                    order by created desc" uuid])))

(defn published?
  "Is the form having `id` published?"
  [form]
  (= :published (:form/status form)))

(defn publish!
  "Change form status to published.
  Takes the user session data and form ID to publish."
  [{user-id :uuid} id]
  (let [{form-not-found :error
         owner :form/owner
         :as form}
        (bugle-forms.model.form/get id)]
    (cond
      form-not-found       {:error :form-not-found}
      (published? form)    {:error :form-already-published}
      (not= owner user-id) {:error :unauthorized-access}
      :else                (sql/update! db/datasource form-table
                                        {:status (jdbc-types/as-other
                                                  "published")}
                                        {:id id}))))
