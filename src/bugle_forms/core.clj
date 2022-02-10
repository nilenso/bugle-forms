(ns bugle-forms.core
  (:require
   [bidi.ring :refer [make-handler]]
   [bugle-forms.config :as config]
   [bugle-forms.migrations :as migrate]
   [bugle-forms.routes :as r]
   [ring.adapter.jetty :as raj])
  (:gen-class))

(def app
  (-> (make-handler r/routes)))

(defonce server (atom nil))

(defn start-server []
  (reset! server
          (raj/run-jetty app
                         {:port (config/get :port)
                          :join? false})))

(defn stop-server []
  (when @server (.stop @server))
  (reset! server nil))

(defn -main
  ([]
   (println "🎺🎺🎺🎺🎺")
   (migrate/migrate)
   (start-server))
  ([cmd migrate-cmd & args]
   (println "🎺🎺🎺🎺🎺")
   (if (= cmd "migrations")
     (apply migrate/cmd-migrate migrate-cmd args)
     (println "invalid option."))))
