(ns bugle-forms.handlers.user
  (:require
   [ring.util.response :as response]))

(defn login [_]
  (response/response "Stub for log-in"))

(defn signup [_]
  (response/response "Stub for sign-up"))
