(ns bugle-forms.db.utils
  (:require
   [camel-snake-kebab.core :as csk]
   [next.jdbc.sql :as sql]))

(defn key-exists? [ds table key val]
  (not-empty
   (sql/find-by-keys ds table {key val}
                     {:table-fn csk/->snake_case
                      :column-fn csk/->snake_case})))
