(ns bugle-forms.middleware
  (:require
   [clojure.stacktrace :as st]
   [clojure.walk :as walk]
   [ring.util.response :as response]))

(defn wrap-exception-handler
  "Middleware that wraps the application in a `try` block.
  Any unhandled exception in the app is caught, and the stacktrace is dumped to
  `*out*`. The user is given a generic status 500 response."
  [handler]
  (fn [request]
    (try
      (handler request)
      (catch Exception e
        (st/print-stack-trace e)
        (-> (response/response "500 Internal Server Error. *sad bugle noises*")
            (response/status 500))))))

(defn wrap-keyword-form-params
  "Middleware that keywordizes form-params in all requests."
  [handler]
  (fn [request]
    (let [request-kw-form-params (update request :form-params
                                         walk/keywordize-keys)]
      (handler request-kw-form-params))))
