(ns bugle-forms.model.user
  (:require
   [buddy.hashers :as hashers]))

(defn signup-params->account
  [{:keys [name email password]}]
  {:user/name name
   :user/email email
   :user/password (hashers/derive password {:alg :argon2id})})
