(ns bugle-forms.routes
  (:require
   [bidi.ring :as br]
   [bugle-forms.handlers.user :as user-handlers]
   [bugle-forms.handlers.utils :as util-handlers]
   [bugle-forms.specs :as specs]
   [clojure.spec.alpha :as s]))

(defn validate-request
  "Guard against malformed requests.
  Takes an map containing a handler and options to validate the request.

  If the `:spec` option is given, the handler is called only when the
  `:request-field` value in the request matches the spec, else a 400 status
  response is returned."
  [handler {:keys [spec request-field]}]
  (fn [request]
    (if-not spec
      (handler request)
      (if (s/valid? spec (get request request-field))
        (handler request)
        (util-handlers/bad-request request)))))

(def routes
  ["/"
   {""       {:get util-handlers/home}
    "signup" {:get user-handlers/signup
              :post (validate-request user-handlers/create-user
                      {:spec ::specs/signup-form
                       :request-field :form-params})}
    "login"  {:get user-handlers/login}
    "public" {:get (br/->Resources {:prefix "public"})}
    true     util-handlers/not-found}])

(def route-handler
  (br/make-handler routes))
