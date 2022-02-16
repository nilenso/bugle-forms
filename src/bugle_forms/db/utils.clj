(ns bugle-forms.db.utils
  (:require
   [camel-snake-kebab.core :as csk]
   [next.jdbc.sql :as sql]))

(defn key-exists?
  "Check if an entry exists in a database.
  Takes a database handle, the name of the table and column as a kebab-case
  keyword, and the value whose existence needs to be checked."
  [ds table key val]
  (not-empty
   (sql/find-by-keys ds table {key val}
                     {:table-fn csk/->snake_case
                      :column-fn csk/->snake_case})))
