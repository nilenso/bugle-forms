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
      :pattern "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{1,63}$"
      :id "email"
      :name "email"
      :type "email")]
    [:div {:id "password"}
     (util/labelled-text-input
      "Set a password"
      :required true
      :minlength 8
      :id "password"
      :name "password"
      :type "password")]
    [:button {:type "submit" :class "btn btn-primary"} "Sign me up!"]]])

(def login
  "Representation of the login form."
  [:div
   [:form {:action "/login" :method "POST"}
    [:div {:id "email"}
     (util/labelled-text-input
      "Email Address"
      :required true
      :pattern "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{1,63}$"
      :id "email"
      :name "email"
      :type "email")]
    [:div {:id "password"}
     (util/labelled-text-input
      "Password"
      :required true
      :id "password"
      :name "password"
      :type "password")]
    [:button {:type "submit" :class "btn btn-primary"} "Log In"]]])
