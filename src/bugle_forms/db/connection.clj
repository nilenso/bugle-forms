(ns bugle-forms.db.connection
  (:require
   [bugle-forms.config :as config]
   [next.jdbc :as jdbc]))

(def db-spec (config/get :db-spec))

(def datasource
  (jdbc/with-options
    (jdbc/get-datasource db-spec)
     jdbc/snake-kebab-opts))
