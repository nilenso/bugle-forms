(ns bugle-forms.core
  (:require [ring.adapter.jetty :as raj])
  (:gen-class))

(defn hello-world [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body
   "<iframe src='//commons.wikimedia.org/wiki/File:FirstCall.ogg?embedplayer=yes'
   width='300' height='20' frameborder='0' webkitAllowFullScreen mozallowfullscreen allowFullScreen>
   </iframe>"})

(defonce server (atom nil))

(defn start-server []
  (reset! server (raj/run-jetty hello-world
                                {:port (or (Integer. (System/getenv "PORT")) 80)
                                 :join? false})))

(defn stop-server []
  (when @server (.stop @server))
  (reset! server nil))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "🎺🎺🎺🎺🎺")
  (start-server))
