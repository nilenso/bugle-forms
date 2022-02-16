(ns bugle-forms.config
  (:refer-clojure :exclude [get])
  (:require
   [aero.core :as aero]
   [clojure.java.io :as io]))

(def environment
  "Contains ENVIRONMENT as keyword if it has been defined, :dev profile if not."
  (or (keyword (clojure.core/get (System/getenv) "ENVIRONMENT"))
      :dev))

(def config
  "Contains the config data."
  (aero/read-config (io/resource "config.edn")
                    {:profile environment}))

(defn get [key]
  (clojure.core/get config key))
