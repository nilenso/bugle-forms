(ns bugle-forms.handlers.user
  (:require
   [bugle-forms.handlers.utils :as util]
   [bugle-forms.model.user :as user]
   [bugle-forms.views.user :as user-views]
   [bugle-forms.views.layout :as layout]
   [ring.util.response :as response]))

(defn login
  "Display login form."
  [{:keys [flash]}]
  (response/response (layout/application
                      {:title "Log In" :flash flash}
                      "Stub for log-in")))

(defn signup
  "Display signup form."
  [{:keys [flash]}]
  (response/response (layout/application
                      {:title "Sign up" :flash flash}
                      user-views/signup)))

(defn create-user
  "Create a user from the signup form parameters in a request."
  [{:keys [form-params]}]
  (let [user (user/create form-params)]
    (if (user/insert! user)
      (-> (response/redirect "/login" :see-other)
          (assoc :flash "Account creation successful!"))
      (util/error-redirect "/signup" "User already exists. Try logging in."))))
