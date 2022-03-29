(ns bugle-forms.handlers.question
  (:require
   [bugle-forms.model.question :as question]
   [ring.util.response :as response])
  (:import
   (java.util UUID)))

(defn add-question
  "Adds a question to a form."
  [{{:keys [form-id]} :route-params
    {question-text :text} :form-params}]
  (let [question (question/create question-text (UUID/fromString form-id))]
    (question/insert! question)
    (response/redirect (str "/form-builder/" form-id) :see-other)))
