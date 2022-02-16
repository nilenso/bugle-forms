(ns bugle-forms.migrations
  (:require
   [bugle-forms.config :as config]
   [migratus.core :as migratus]))

(def migration-config
  {:store :database
   :migration-dir "migrations"
   :db (config/get :db-spec)})

(defn create [name]
  (migratus/create migration-config name))

(defn migrate []
  (migratus/migrate migration-config))

(defn rollback []
  (migratus/rollback migration-config))

(defn up [ids]
  (apply migratus/up migration-config ids))

(defn down [ids]
  (apply migratus/down migration-config ids))

(defn cmd-migrate
  "Utility to perform migrations from the command line."
  [command & args]
  (case command
    "create"   (create (first args))
    "migrate"  (migrate)
    "rollback" (rollback)
    "up"       (up (map #(Long/parseLong %) args))
    "down"     (down (map #(Long/parseLong %) args))))
