(ns bugle-forms.model.user
  (:require
   [buddy.hashers :as hashers]
   [bugle-forms.db.connection :as db]
   [next.jdbc.sql :as sql]
   [next.jdbc.plan :as plan])
  (:import
   (java.util Base64)
   (java.security SecureRandom)))

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

(defn generate-session-id
  "Generate a cryptographically secure random session ID."
  []
  (let [random (SecureRandom.)
        base64 (.withoutPadding (Base64/getUrlEncoder))
        buffer (byte-array 32)]
    (.nextBytes random buffer)
    (.encodeToString base64 buffer)))

(defn authenticate
  "Authenticate user based on provided login credentials.
  Returns a map containing username and a random session ID if successful, or an
  error value otherwise."
  [{email :email, given-pwd :password}]
  (if-let [{pwd :password, name :name, uuid :id}
           (plan/select-one!
            db/datasource [:id :name :password]
            ["select id, name, password from user_account where email = ?" email])]
    (if (:valid (hashers/verify given-pwd pwd))
      {:id (generate-session-id)
       :uuid uuid
       :name name}
      {:error :invalid-password})
    {:error :no-user-found}))
