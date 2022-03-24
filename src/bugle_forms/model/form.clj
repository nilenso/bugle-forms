(ns bugle-forms.model.form
  (:refer-clojure :exclude [get])
  (:require
   [bugle-forms.db.connection :as db]
   [next.jdbc.plan :as plan]
   [next.jdbc.sql :as sql]
   [next.jdbc.date-time])
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
   :form/created (Instant/now)})

(defn insert!
  "Insert form in database."
  [form]
  (sql/insert! db/datasource form-table form))

(defn get
  "Retrieve form details."
  [id]
  (if-some [form (sql/get-by-id db/datasource form-table id)]
    form
    {:error "Form not found."}))

(defn get-forms
  "Retrieve forms belonging to a user."
  [{:keys [uuid]}]
  (plan/select!
   db/datasource
   [:id :name :created]
   ["select id, name, created from form where owner = ? order by created desc" uuid]))
