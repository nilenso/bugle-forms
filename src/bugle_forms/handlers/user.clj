(ns bugle-forms.handlers.user
  (:require
   [ring.util.response :as response]))

(defn signin [_]
  (response/response "Stub for sign-in"))

(defn signup [_]
  (response/response "Stub for sign-up"))
