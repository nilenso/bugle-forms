(ns bugle-forms.app
  (:require
   [bugle-forms.config :as config]
   [bugle-forms.middleware :as m]
   [bugle-forms.migrations :as migrate]
   [bugle-forms.handler-dispatch :as dispatch]
   [mount.core :as mount :refer [defstate]]
   [ring.middleware.flash :refer [wrap-flash]]
   [ring.middleware.params :refer [wrap-params]]
   [ring.middleware.session :refer [wrap-session]]
   [ring.adapter.jetty :as raj])
  (:gen-class))

(def app
  (-> dispatch/route-handler
      wrap-params
      wrap-flash
      wrap-session
      m/wrap-exception-handler))

(defn start-server []
  (raj/run-jetty app
                 {:port (config/get :port)
                  :join? false}))

(defn stop-server [server]
  (when server
    (.stop server)))

(defstate server
  :start (start-server)
  :stop (stop-server server))

(defn -main
  ([]
   (println "🎺🎺🎺🎺🎺")
   (mount/start)
   (migrate/migrate))
  ([cmd migrate-cmd & args]
   (println "🎺🎺🎺🎺🎺")
   (if (= cmd "migrations")
     (apply migrate/cmd-migrate migrate-cmd args)
     (println "invalid option."))))
