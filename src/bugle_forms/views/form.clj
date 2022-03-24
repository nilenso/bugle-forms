(ns bugle-forms.views.form
  (:require
   [bugle-forms.views.utils :as util])
  (:import
   (java.time ZoneId)
   (java.time.format DateTimeFormatter)))

(def add-form
  "Representation for form creation form."
  [:form {:action "/form" :method :post}
   [:span {:class "builder-control"}
    (util/labelled-text-input
     "Create Form"
     :id "name"
     :name "name"
     :type "text"
     :placeholder "Enter form name...")
    [:button {:type "submit" :class "btn"} "→"]]])

(def form->html
  "TODO: Add form questions"
  (constantly [:p "Form questions will appear here."]))

(defn formatted-time
  "Returns a human readable timestamp from a JDBC SQL timestamp."
  [timestamp]
  (let [formatter (-> (DateTimeFormatter/ofPattern "dd-MM-yyyy, HH:mm")
                      (.withZone (ZoneId/of "UTC")))]
    (.format formatter (.toInstant timestamp))))

(defn list-forms
  "Show a list of all forms belonging to a user."
  [forms]
  (list
   [:div {:class "form-list-header"} "Form name" [:span "Created on"]]
   [:hr]
   (map (fn [form]
          [:div {:class "form-list-item"}
           [:a {:href (str "/form-builder/" (:id form))} (:name form)]
           [:span (formatted-time (:created form))]])
        forms)))

(defn form-builder
  "Generate representation of form builder."
  [form]
  [:div
   (form->html form)
   [:form {:method "post"}
    [:label {:for "question-input" :class "form-label"}
     "Enter your question:"]
    [:input {:class "form-control" :type "text"
             :name "question-input" :id "question-input"}]
    [:span {:class "builder-control"}
     [:button {:formaction "/question" :type "button"
               :id "add-question" :class "btn"}
      "+ Add"]]]])
