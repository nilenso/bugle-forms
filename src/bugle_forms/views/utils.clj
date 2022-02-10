(ns bugle-forms.views.utils
  (:require
   [camel-snake-kebab.core :as csk]))

(defn labelled-text-input [label & args]
  (let [options (apply hash-map args)
        name (or (:name options)
                 (csk/->kebab-case-string label))]
    [:div
     [:label {:for name :class "form-label"} label]
     [:input (merge {:class "form-control" :type "text" :id name :name name}
                    options)]]))
