(ns bugle-forms.middleware
  (:require
   [bidi.bidi :as bidi]
   [bugle-forms.routes :as routes]
   [bugle-forms.handlers.utils :as util-handlers]
   [clojure.spec.alpha :as s]
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

(defn validate-request
  "Guard against malformed requests.
  Takes an map containing a handler and options to validate the request.

  If the `:spec` option is given, the handler is called only when the `:field`
  value in the request matches the spec, else a 400 status response is
  returned."
  [handler {:keys [spec field no-keywordize-field]}]
  (let [handler (if no-keywordize-field
                  handler
                  (wrap-keyword-form-params handler))]
    (fn [request]
      (if-not spec
        (handler request)
        (if (s/valid? spec (get request field))
          (handler request)
          (util-handlers/bad-request request))))))

(defn has-access?
  "Does this user have access to a resource?"
  [user-type session-info]
  (case user-type
    :member (when session-info true)
    :guest (when-not session-info true)
    ;; TODO: Use a logging statement rather than a print.
    ;; We don't have a logging system in place yet, so this a stopgap.
    (println "Error: Unknown user-type: " user-type)))

(def access-control-redirects
  {:member {:route ::routes/login
            :flash "You need to log in to view this resource."}
   :guest  {:route ::routes/dashboard}})

(defn resolve-access-redirect
  [user-type]
  (let [redirect-info (access-control-redirects user-type)]
    (assoc redirect-info
           :path (bidi/path-for routes/routes (:route redirect-info)))))

(defn wrap-access-control
  "Sets up redirects if a user has no access to the resource in a handler."
  [handler {user-type :needs}]
  (fn [{{session-info :user} :session :as request}]
    (let [{:keys [path flash]} (resolve-access-redirect user-type)
          redirect (if flash
                     (partial util-handlers/flash-redirect path flash)
                     (partial response/redirect path :see-other))]
      (if-not (has-access? user-type session-info)
        (redirect)
        (handler request)))))
