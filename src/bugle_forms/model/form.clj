(ns bugle-forms.model.form
  (:refer-clojure :exclude [get])
  (:require
   [bugle-forms.db.connection :as db]
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
