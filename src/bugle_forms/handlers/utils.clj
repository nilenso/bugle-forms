(ns bugle-forms.handlers.utils
  (:require
   [ring.util.response :as response]))

(defn error-redirect [route message]
  (-> (response/redirect route)
      (assoc :flash message)))
