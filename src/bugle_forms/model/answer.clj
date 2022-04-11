(ns bugle-forms.model.answer
  (:import
   [java.util UUID]
   [java.time Instant]))

(defn create
  "Create an answer for a given response"
  [response-id question-id text]
  {:answer/id (UUID/randomUUID)
   :answer/response-id response-id
   :answer/question-id question-id
   :answer/text text
   :answer/created (Instant/now)
   :answer/updated (Instant/now)})
