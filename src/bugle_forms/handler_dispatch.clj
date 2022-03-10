(ns bugle-forms.handler-dispatch
  (:require
   [bidi.ring]
   [bugle-forms.handlers.utils :as util]
   [bugle-forms.middleware :as mw]
   [bugle-forms.routes :as routes]))

(defn handler-from-spec
  "Parses a handler spec and returns a handler with the specified options."
  [handler-key]
  (if (fn? handler-key)
    ;; If our dispatch object is a function, we assume it to be a
    ;; ring handler and call it directly.
    handler-key
    (let [handler-spec (routes/handler-specs handler-key)
          {handler       :handler
           validate-opts :validate
           access-opts   :access-control} handler-spec]
      (if handler-spec
        (cond-> handler
          validate-opts (mw/validate-request validate-opts)
          access-opts (mw/wrap-access-control access-opts))
        util/error-response))))

(def route-handler
  (bidi.ring/make-handler routes/routes handler-from-spec))
