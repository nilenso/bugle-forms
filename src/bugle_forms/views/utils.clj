(ns bugle-forms.views.utils
  (:require
   [camel-snake-kebab.core :as csk]))

(defn labelled-text-input
  "Create a representation of a labelled input box.
  Takes a label along with an option map which are added as CSS attributes."
  [label & {:keys [name label-class] :as opts
            :or {name (csk/->kebab-case-string label)
                 label-class "form-label"}}]
  [:div
   [:label {:for name :class label-class} label]
   [:input (merge {:class "form-control" :type "text" :id name :name name}
                  opts)]])
