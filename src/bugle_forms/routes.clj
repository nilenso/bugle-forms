(ns bugle-forms.routes
  (:require
   [bugle-forms.handlers.core :as core-handlers]
   [bugle-forms.handlers.user :as user-handlers]))

(def routes
  ["/"
   {""       {:get core-handlers/home}
    "signup" {:get user-handlers/signup}
    "signin" {:get user-handlers/signin}
    true     core-handlers/not-found}])
