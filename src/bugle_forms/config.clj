(ns bugle-forms.config
  (:refer-clojure :exclude [get])
  (:require
   [aero.core :as aero]
   [clojure.java.io :as io]))

(def environment
  (or (keyword (clojure.core/get (System/getenv) "ENVIRONMENT"))
      :dev))

(def config
  (aero/read-config (io/resource "config.edn")
                    {:profile environment}))

(defn get [key]
  (clojure.core/get config key))
