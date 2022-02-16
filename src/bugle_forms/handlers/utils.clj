(ns bugle-forms.handlers.utils
  (:require
   [ring.util.response :as response]))

(defn error-redirect
  "Redirect to another route with a flash message.
  Use this to redirect to another page when something goes wrong."
  [route message]
  (-> (response/redirect route)
      (assoc :flash message)))
