(ns bugle-forms.routes
  (:require
   [bidi.ring :as br]
   [bugle-forms.handlers.core :as core-handlers]
   [bugle-forms.handlers.user :as user-handlers]))

(def routes
  ["/"
   {""       {:get core-handlers/home}
    "signup" {:get user-handlers/signup}
    "login"  {:get user-handlers/login}
    "public" {:get (br/->Resources {:prefix "public"})}
    true     core-handlers/not-found}])
