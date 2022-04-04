(ns bugle-forms.views.form
  (:require
   [bugle-forms.model.form :as form]
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
     :placeholder "Enter form name..."
     :required true
     :pattern ".*\\S+.*")
    [:button {:type "submit" :class "btn"} "→"]]])

(defn list-questions
  "Returns representation of form questions."
  [questions]
  (if (empty? questions)
      [:p "Form questions will appear here."]
      [:div {:class "question-view"}
       [:h4 "Questions"]
       [:ul
        (map (fn [question]
               [:li (:text question)])
             questions)]]))

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
   [:div {:class "form-list-header"}
    "Form name" [:span "Created on"]]
   [:hr]
   (map (fn [form]
          [:div {:class "form-list-item"}
           [:a {:href (str "/form-builder/" (:form/id form))}
            (:form/name form)]
           (when-let [form-link (form/link form)]
             [:span {:class "share-link-cell"}
              [:a {:href form-link} "[share link]"]])
           [:span {:class "timestamp-cell"}
            (formatted-time (:form/created form))]])
        forms)))

(defn form-builder
  "Generate representation of form builder."
  [form-id questions]
  (let [form (form/get form-id)]
    [:div
     (list-questions questions)
     (if-not (form/published? form)
       (list [:form {:method "post"}
              [:label {:for "text" :class "form-label"}
               "Enter your question:"]
              [:span {:class "builder-control" :id "question-input"}
               [:input {:class "form-control"
                        :type "text"
                        :name "text"
                        :id "text"
                        :required true
                        :pattern ".*\\S+.*"}]
               [:button {:formaction (str "/form/" form-id "/question")
                         :type "submit"
                         :id "add-question" :class "btn"}
                "+"]]]
             [:form {:method "post"}
              [:span {:class "builder-control"}
               [:button {:formaction (str "/form/" form-id "/publish")
                         :type "submit"
                         :id "publish-form" :class "btn"}
                "Publish"]]])
       [:span {:class "builder-control"}
        [:h3 [:a {:href (form/link form)} "[share link]"]]])]))
