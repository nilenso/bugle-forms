(ns bugle-forms.handlers.form
  (:require
   [bugle-forms.model.form :as form]
   [bugle-forms.model.question :as question]
   [bugle-forms.views.form :as form-views]
   [bugle-forms.views.layout :as layout]
   [bugle-forms.handlers.utils :as util]
   [ring.util.response :as response])
  (:import
   [java.util UUID]))

(defn form-builder
  "Return form builder page."
  [{{:keys [id]} :route-params
    {:keys [user]} :session
    :keys [flash]}]
  (let [form-id (UUID/fromString id)
        {:keys [form/name error]} (form/get form-id)
        questions (question/get-questions form-id)]
    (if error
      (response/not-found "404. Form not found.")
      (response/response (layout/application
                          {:title (str "Editing: " name)
                           :user user
                           :flash flash}
                          (form-views/form-builder
                           form-id questions))))))

(defn create
  "Create new form for the user"
  [{{:keys [user]} :session
    {:keys [name]} :form-params}]
  (let [form (form/create name (:uuid user))]
    (if (form/insert! form)
      (util/flash-redirect (str "/form-builder/" (:form/id form))
                           "Form creation successful.")
      (util/flash-redirect "/dashboard" "Something went wrong."))))

(defn publish
  "Publish a form"
  [{{:keys [user]} :session
    {form-id-str :form-id} :route-params}]
  (let [form-id (UUID/fromString form-id-str)]
    (case (form/publish! user form-id)
      {:error :unauthorized-access}
      (util/flash-redirect "/dashboard" "Something went wrong.")

      {:error :form-already-published}
      (util/flash-redirect (str "/form-builder/" form-id-str)
                           "Form already published.")

      (util/flash-redirect "/dashboard"
                           (list "Successfully published!"
                                 [:a {:href (str "/form/" form-id-str)}
                                  " [Share Link]"])))))
