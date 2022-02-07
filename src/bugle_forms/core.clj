(ns bugle-forms.core
  (:require
   [bidi.ring :refer [make-handler]]
   [bugle-forms.routes :as r]
   [ring.adapter.jetty :as raj]
   [ring.middleware.resource :as res])
  (:gen-class))

(def app
  (-> (make-handler r/routes)
      (res/wrap-resource "")))

(defonce server (atom nil))

(defn start-server []
  (reset! server
          (raj/run-jetty app
                         {:port (Integer. (or (System/getenv "PORT") "8080"))
                          :join? false})))

(defn stop-server []
  (when @server (.stop @server))
  (reset! server nil))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "🎺🎺🎺🎺🎺")
  (start-server))
