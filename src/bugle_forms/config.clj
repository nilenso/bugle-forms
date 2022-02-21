(ns bugle-forms.config
  (:refer-clojure :exclude [get])
  (:require
   [aero.core :as aero]
   [clojure.java.io :as io]
   [mount.core :refer [defstate]]))

(defstate environment
  "Contains ENVIRONMENT as keyword if it has been defined, :dev profile if not."
  :start (or (keyword (clojure.core/get (System/getenv) "ENVIRONMENT"))
             :dev)
  :stop nil)

(defstate config
  "Contains the config data."
  :start (aero/read-config (io/resource "config.edn")
                           {:profile environment})
  :stop nil)

(defn get [key]
  (clojure.core/get config key))
