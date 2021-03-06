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

(s/def :form/id uuid?)
(s/def :form/name (s/and string? seq))
(s/def :form/owner uuid?)
(s/def :form/status #{:draft :published})
(s/def :form/created inst?)

(s/def ::form
  (s/keys :req [:form/id
                :form/name
                :form/status
                :form/owner
                :form/created]))

(s/def ::form-creation-form
  (s/keys :req-un [:form/name]))

(s/def :question/id uuid?)
(s/def :question/form-id uuid?)
(s/def :question/text (s/and string? seq))
(s/def :question/created inst?)
(s/def :question/updated inst?)

(s/def ::question
  (s/keys :req [:question/id
                :question/form-id
                :question/text
                :question/created
                :question/updated]))

(s/def ::add-question-form
  (s/keys :req-un [:question/text]))

(s/def :response/id uuid?)
(s/def :response/form-id uuid?)
(s/def :response/created inst?)
(s/def :response/updated inst?)

(s/def ::response
  (s/keys :req [:response/id
                :response/form-id
                :response/created
                :response/updated]))
