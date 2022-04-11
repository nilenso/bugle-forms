(ns bugle-forms.model.question
  (:require
   [bugle-forms.db.connection :as db]
   [next.jdbc.plan :as plan]
   [next.jdbc.sql :as sql]
   [next.jdbc.date-time])
  (:import
   (java.util UUID)
   (java.time Instant)))

(defn create
  "Make a question object suitable to be inserted in database.
  Takes form ID to set the form associated with the question."
  [text form-id]
  {:question/id (UUID/randomUUID)
   :question/form-id form-id
   :question/text text
   :question/created (Instant/now)
   :question/updated (Instant/now)})

(defn insert!
  "Insert question in database."
  [question]
  (sql/insert! db/datasource :question question))

(defn get-questions
  "Retrieve questions for a given form"
  [form-id]
  (plan/select!
   db/datasource [:question/id :question/text]
   ["select id, text from question where form_id = ?" form-id]))
