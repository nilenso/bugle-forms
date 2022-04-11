(ns bugle-forms.model.response
  (:require
   [bugle-forms.db.connection :as db]
   [bugle-forms.model.answer :as answer]
   [next.jdbc.sql :as sql])
  (:import
   [java.util UUID]
   [java.time Instant]))

(defn create-answers
  "Get a collection of answers from form params and response."
  [form-params {response-id :response/id :as _response}]
  (map (fn [[question-id text]]
         (answer/create response-id (UUID/fromString question-id) text))
       form-params))

(defn insert-answers!
  "Inserts a collection of answers into the database."
  [answers]
  (let [cols (keys (first answers))
        rows (map (apply juxt cols) answers)]
    (sql/insert-multi! db/datasource :answer cols rows)))

(defn create
  "Create a response for a form."
  [form]
  {:response/id (UUID/randomUUID)
   :response/form-id (:form/id form)
   :response/created (Instant/now)
   :response/updated (Instant/now)})

(defn insert!
  "Insert response in the database."
  [response]
  (sql/insert! db/datasource :response response))

(defn create-and-store!
  "Create and stores a response entry, along with answers submitted by the user.
  Takes form parameters resulting from a form submission, and the form that the
  response corresponds to."
  [form-params form]
  (let [{response-id :response/id :as response} (create form)
        answers (create-answers form-params response)]
    (insert! response)
    (insert-answers! answers)
    {:response-id response-id}))
