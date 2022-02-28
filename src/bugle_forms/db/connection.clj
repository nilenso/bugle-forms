(ns bugle-forms.db.connection
  (:require
   [bugle-forms.config :as config]
   [mount.core :refer [defstate]]
   [next.jdbc :as jdbc]))

(defstate db-spec
  :start (config/get :db-spec)
  :stop nil)

(defstate datasource
  :start (jdbc/with-options
           (jdbc/get-datasource db-spec)
           jdbc/snake-kebab-opts)
  :stop nil)
