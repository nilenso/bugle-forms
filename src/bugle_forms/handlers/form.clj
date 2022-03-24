(ns bugle-forms.handlers.form
  (:require
   [bugle-forms.model.form :as form]
   [bugle-forms.views.form :as form-views]
   [bugle-forms.views.layout :as layout]
   [bugle-forms.handlers.utils :as util]
   [ring.util.response :as response]))

(defn form-builder
  "Return form builder page."
  [{{:keys [id]} :route-params
    {:keys [user]} :session}]
  (let [{:keys [form/questions form/name error]} (form/get (java.util.UUID/fromString id))]
    (if error
      (response/not-found "404. Form not found.")
      (response/response (layout/application
                          {:title (str "Editing: " name)
                           :user user}
                          (form-views/form-builder
                           questions))))))

(defn create
  "Create new form for the user"
  [{{:keys [user]} :session
    {:keys [name]} :form-params}]
  (let [form (form/create name (:uuid user))]
    (if (form/insert! form)
      (util/flash-redirect (str "/form-builder/" (:form/id form))
                           "Form creation successful.")
      (util/flash-redirect "/dashboard" "Something went wrong."))))