(ns bugle-forms.model.user
  (:require
   [buddy.hashers :as hashers]
   [bugle-forms.db.connection :as db]
   [next.jdbc.sql :as sql]))

(def user-table :user-account)

(defn create
  "Make a user account suitable to be inserted in database.
  Takes signup form params and creates a user account with the password hashed
  using Argon2id."
  [{:keys [name email password]}]
  {:user/name name
   :user/email email
   :user/password (hashers/derive password {:alg :argon2id})})

(defn in-database?
  [{:user/keys [email]}]
  (not-empty (sql/find-by-keys db/datasource user-table {:email email})))

(defn insert!
  "Insert user in database."
  [user]
  (when-not (in-database? user)
    (sql/insert! db/datasource user-table user)))
