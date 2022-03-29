(ns bugle-forms.factories
  (:require
   [bugle-forms.specs :as specs]
   [clojure.spec.alpha :as s]
   [clojure.spec.gen.alpha :as gen]))

(defn user
  "Randomly generate a user.
  Optionally takes a map of overrides."
  ([] (user {}))
  ([overrides]
   (gen/generate
    (s/gen ::specs/user-account
           (into {} (map (fn [[k v]]
                           {k (fn [] (gen/return v))})
                         overrides))))))

(defn form
  "Randomly generate a form.
  Optionally takes a map of overrides."
  ([] (form {}))
  ([overrides]
   (gen/generate
    (s/gen ::specs/form
           (into {} (map (fn [[k v]]
                           {k (fn [] (gen/return v))})
                         overrides))))))

(defn create-form-params
  "Randomly generate form params for creating form.
  Optionally takes a map of overrides."
  ([] (create-form-params {}))
  ([overrides]
   (gen/generate
    (s/gen ::specs/form-creation-form
           (into {} (map (fn [[k v]]
                           {k (fn [] (gen/return v))})
                         overrides))))))
(defn question
  "Randomly generate a question.
  Optionally takes a map of overrides."
  ([] (question {}))
  ([overrides]
   (gen/generate
    (s/gen ::specs/question
           (into {} (map (fn [[k v]]
                           {k (fn [] (gen/return v))})
                         overrides))))))

(defn question-form-params
  "Randomly generate form params for creating form.
  Optionally takes a map of overrides."
  ([] (question-form-params {}))
  ([overrides]
   (gen/generate
    (s/gen ::specs/add-question-form
           (into {} (map (fn [[k v]]
                           {k (fn [] (gen/return v))})
                         overrides))))))
