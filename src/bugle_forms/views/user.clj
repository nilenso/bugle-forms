(ns bugle-forms.views.user
  (:require
   [bugle-forms.views.utils :as util]))

(def signup
  "Representation of the signup form."
  [:div
   [:form {:action "/signup" :method "POST"}
    [:div {:id "name"}
     (util/labelled-text-input
      "What's your name?"
      :required true
      :id "name"
      :name "name"
      :type "text")]
    [:div {:id "email"}
     (util/labelled-text-input
      "Email Address"
      :required true
      :id "email"
      :name "email"
      :type "email")]
    [:div {:id "password"}
     (util/labelled-text-input
      "Set a password"
      :required true
      :id "password"
      :name "password"
      :type "password")]
    [:button {:type "submit" :class "btn btn-primary"} "Sign me up!"]]])
