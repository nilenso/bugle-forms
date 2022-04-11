(ns bugle-forms.views.response
  (:require
   [bugle-forms.model.question :as question]
   [bugle-forms.views.utils :as util]))

(defn response-form
  "Representation of form response"
  [{form-id :form/id}]
  [:form {:action (str "/form/" form-id "/response")
          :method "post"}
   (map (fn [{question-text :question/text
              question-id :question/id}]
          (util/labelled-text-input
           question-text
           :label-class "response-form-label"
           :name question-id
           :id question-id
           :type "text"))
        (question/get-questions form-id))
   [:button {:type "submit" :class "btn" :id "submit-response"} "Submit"]])
