(ns bugle-forms.handlers.response
  (:require
   [bugle-forms.model.form :as form]
   [bugle-forms.model.response :as response]
   [bugle-forms.views.layout :as layout]
   [bugle-forms.views.response :as response-views]
   [ring.util.response :as ring-response])
  (:import
   [java.util UUID]))

(defn response-form
  "Displays form for collecting response."
  [{{form-id-str :form-id} :route-params
    {:keys [user]} :session
    :keys [flash]}]
  (let [form-id (UUID/fromString form-id-str)
        {:keys [form/name error] :as form} (form/get form-id)]
    (if error
      (ring-response/not-found "404. Form not found.")
      (ring-response/response (layout/application
                               {:title (str "Response for: " name)
                                :user user
                                :flash flash}
                               (response-views/response-form form))))))

(defn create
  "Inserts the response submitted by user to the backend."
  [{{form-id-str :form-id} :route-params
    {:keys [user]} :session
    :keys [form-params]}]
  (let [form-id (UUID/fromString form-id-str)
        {:keys [error] :as form} (form/get form-id)]
    (if error
      (ring-response/not-found "404. Form not found.")
      (do (response/create-and-store! form-params form)
          (ring-response/response (layout/application
                                   {:title "Form Submitted!"
                                    :user user}
                                   [:p "Form successfully submitted!"]))))))
