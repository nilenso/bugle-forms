(ns bugle-forms.model.user
  (:require
   [buddy.hashers :as hashers]))

(defn signup-params->account
  "Make a user account suitable to be inserted in database.
  Takes signup form params and creates a user account with the password hashed
  using Argon2id."
  [{:keys [name email password]}]
  {:user/name name
   :user/email email
   :user/password (hashers/derive password {:alg :argon2id})})
