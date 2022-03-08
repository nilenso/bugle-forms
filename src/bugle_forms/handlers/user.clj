(ns bugle-forms.handlers.user
  (:require
   [bugle-forms.handlers.utils :as util]
   [bugle-forms.model.user :as user]
   [bugle-forms.views.user :as user-views]
   [bugle-forms.views.layout :as layout]
   [ring.util.response :as response]))

(defn login-form
  "Display login form."
  [{:keys [flash], {:keys [user]} :session}]
  (response/response (layout/application
                      {:title "Log In" :flash flash :user user}
                      user-views/login)))

(defn signup
  "Display signup form."
  [{:keys [flash], {:keys [user]} :session}]
  (response/response (layout/application
                      {:title "Sign up" :flash flash}
                      user-views/signup)))

(defn login
  "Create a session on a successful login."
  [{:keys [form-params session]}]
  (let [session-data (user/authenticate form-params)]
    (if (:error session-data)
      (util/flash-redirect
       "/login" "Login failed; Invalid email or password.")
      (-> (response/redirect "/dashboard" :see-other)
          (assoc :session (merge session {:user session-data}))))))

(defn create-user
  "Create a user from the signup form parameters in a request."
  [{:keys [form-params]}]
  (let [user (user/create form-params)]
    (if (user/insert! user)
      (-> (response/redirect "/login" :see-other)
          (assoc :flash "Account creation successful!"))
      (util/flash-redirect "/signup" "User already exists. Try logging in."))))

(defn logout
  "Log the user out; deletes session."
  [{{:keys [user]} :session}]
  (-> (util/flash-redirect "/" "Logged out successfully.")
      (assoc :session nil)))

(defn dashboard
  "Display a user's dashboard."
  [{:keys [flash], {:keys [user]} :session}]
  (response/response (layout/application
                      {:title "Dashboard" :flash flash :user user}
                      (format "Stub for dashboard. Hi, %s!" (:name user)))))
