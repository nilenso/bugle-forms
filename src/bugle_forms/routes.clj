(ns bugle-forms.routes
  (:require
   [bidi.ring]
   [bugle-forms.handlers.form :as form-handlers]
   [bugle-forms.handlers.question :as question-handlers]
   [bugle-forms.handlers.response :as response-handlers]
   [bugle-forms.handlers.user :as user-handlers]
   [bugle-forms.handlers.utils :as util-handlers]
   [bugle-forms.specs :as specs]))

(def routes
  ["/"
   [["" {:get ::home}]
    ["signup" {:get  ::signup
               :post ::create-user}]
    ["login" {:get  ::login-form
              :post ::login}]
    ["logout" {:get ::logout}]
    ["dashboard" {:get ::dashboard}]
    [["form-builder/" :id] {:get ::form-builder}]
    ["form" {"" {:post ::create-form}
             ["/" :form-id] {:get ::response-form}
             ["/" :form-id "/question"] {:post ::add-question}
             ["/" :form-id "/publish"] {:post ::publish-form}
             ["/" :form-id "/response"] {:post ::create-response}}]
    ["public" {:get (bidi.ring/->Resources {:prefix "public"})}]
    [true ::not-found]]])

(def handler-specs
  "Specification of handlers for a matched route.
  Contains guards for structural validation and access control. Not to be
  confused with Clojure specs."
  {::home          {:handler util-handlers/home}
   ::signup        {:handler user-handlers/signup
                    :access-control {:needs :guest}}
   ::create-user   {:handler user-handlers/create-user
                    :validate {:spec  ::specs/signup-form
                               :field :form-params}}
   ::login-form    {:handler user-handlers/login-form
                    :access-control {:needs :guest}}
   ::login         {:handler user-handlers/login
                    :access-control {:needs :guest}
                    :validate {:spec  ::specs/login-form
                               :field :form-params}}
   ::logout        {:handler user-handlers/logout
                    :access-control {:needs :member}}
   ::dashboard     {:handler user-handlers/dashboard
                    :access-control {:needs :member}}
   ::create-form   {:handler form-handlers/create
                    :access-control {:needs :member}
                    :validate {:spec  ::specs/form-creation-form
                               :field :form-params}}
   ::form-builder  {:handler form-handlers/form-builder
                    :access-control {:needs :member}}
   ::add-question  {:handler question-handlers/add-question
                    :access-control {:needs :member}
                    :validate {:spec ::specs/add-question-form
                               :field :form-params}}
   ::publish-form  {:handler form-handlers/publish
                    :access-control {:needs :member}}
   ::response-form {:handler response-handlers/response-form
                    :access-control {:needs :member}}
   ::create-response {:handler response-handlers/create
                      :validate {:spec ::specs/create-response-form-params
                                 :field :form-params
                                 :no-keywordize-field true}
                      ;; reconsider access control
                      ;; {:permitted-roles #{:member ...}}
                      }
   ::not-found     {:handler util-handlers/not-found}})
