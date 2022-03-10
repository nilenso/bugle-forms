(ns bugle-forms.specs
  (:require
   [clojure.spec.alpha :as s]
   [clojure.spec.gen.alpha :as gen]))

(def ^:private
  non-empty-alphanumeric-string
  (gen/not-empty (gen/string-alphanumeric)))

(defn- gen-string-in-range [start end]
  (gen/fmap #(apply str %)
            (gen/vector (gen/char-alphanumeric) start end)))

(def email-regex #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{1,63}$")
(defn- email-generator []
  (gen/fmap
   (fn [[name host tld]]
     (str name "@" host "." tld))
   (gen/tuple non-empty-alphanumeric-string
              non-empty-alphanumeric-string
              (gen-string-in-range 1 10))))

(s/def :user/name (s/and string? seq))
(s/def :user/email (s/with-gen (s/and string? #(re-matches email-regex %))
                     email-generator))
(s/def :user/password (s/and string? #(> (count %) 7)))

(s/def ::signup-form
  (s/keys :req-un [:user/name
                   :user/email
                   :user/password]))

(s/def ::login-form
  (s/keys :req-un [:user/email
                   :user/password]))

(s/def ::user-account
  (s/keys :req [:user/name
                :user/email
                :user/password]))
