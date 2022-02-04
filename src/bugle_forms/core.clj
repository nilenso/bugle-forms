(ns bugle-forms.core
  (:require
   [bugle-forms.views.layout :as layout]
   [bugle-forms.views.home :as home]
   [ring.adapter.jetty :as raj]
   [ring.middleware.resource :refer [wrap-resource]]
   [ring.util.response :as response]
   [hiccup.page :refer [html5]])
  (:gen-class))

(defn home [request]
  (response/response
   (html5 (layout/application "Bugle Forms" home/content))))

(defonce server (atom nil))

(defn start-server []
  (reset! server (raj/run-jetty (wrap-resource home "/")
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
