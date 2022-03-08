(ns bugle-forms.routes
  (:require
   [bidi.ring]
   [bugle-forms.handlers.user :as user-handlers]
   [bugle-forms.handlers.utils :as util-handlers]
   [bugle-forms.specs :as specs]))

(def routes
  ["/"
   {""          {:get ::home}
    "signup"    {:get  ::signup
                 :post ::create-user}
    "login"     {:get  ::login-form
                 :post ::login}
    "logout"    {:get ::logout}
    "dashboard" {:get ::dashboard}
    "public"    {:get (bidi.ring/->Resources {:prefix "public"})}
    true        ::not-found}])

(def handler-specs
  "Specification of handlers for a matched route.
  Contains guards for structural validation and access control. Not to be
  confused with Clojure specs."
  {::home        {:handler util-handlers/home}
   ::signup      {:handler user-handlers/signup}
   ::create-user {:handler user-handlers/create-user
                  :validate {:spec  ::specs/signup-form
                             :field :form-params}}
   ::login-form  {:handler user-handlers/login-form
                  :access-control {:needs :guest}}
   ::login       {:handler user-handlers/login
                  :access-control {:needs :guest}
                  :validate {:spec  ::specs/login-form
                             :field :form-params}}
   ::logout      {:handler  user-handlers/logout
                  :access-control {:needs :member}}
   ::dashboard   {:handler user-handlers/dashboard
                  :access-control {:needs :member}}
   ::not-found   {:handler util-handlers/not-found}})
