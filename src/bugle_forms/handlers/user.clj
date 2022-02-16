(ns bugle-forms.handlers.user
  (:require
   [bugle-forms.db.connection :as db]
   [bugle-forms.db.utils :as dbutil]
   [bugle-forms.handlers.utils :as util]
   [bugle-forms.model.user :as user]
   [bugle-forms.specs :as specs]
   [bugle-forms.views.user :as user-views]
   [bugle-forms.views.layout :as layout]
   [camel-snake-kebab.core :as csk]
   [clojure.spec.alpha :as s]
   [hiccup.page :refer [html5]]
   [next.jdbc.sql :as sql]
   [ring.util.response :as response]))

(defn login
  "Display login form."
  [{:keys [flash]}]
  (response/response
   (html5 (layout/application {:title "Log In" :flash flash}
                              "Stub for log-in"))))

(defn signup
  "Display signup form."
  [{:keys [flash]}]
  (response/response
   (html5 (layout/application {:title "Sign up" :flash flash}
                              user-views/signup))))

(defn create-user
  "Create a user from the signup form parameters in a request."
  [{:keys [form-params]}]
  (let [valid? (s/valid? ::specs/signup-form form-params)
        account (and valid? (user/signup-params->account form-params))]
    (cond
      (not valid?)
      (util/error-redirect "/signup" "Invalid form submission.")

      (dbutil/key-exists? db/datasource :user-account
                          :email (:user/email account))
      (util/error-redirect "/signup" "User already exists.")

      :else
      (do
        (sql/insert! db/datasource :user-account account
                     {:table-fn csk/->snake_case})
        (-> (response/redirect "/login" :see-other)
            (assoc :flash "Account creation successful!"))))))
