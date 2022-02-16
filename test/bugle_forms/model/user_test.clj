(ns bugle-forms.model.user-test
  (:require
   [bugle-forms.specs :as specs]
   [bugle-forms.model.user :as user]
   [clojure.spec.alpha :as cljspec]
   [clojure.test.check.clojure-test :refer [defspec]]
   [clojure.test.check.properties :as prop]))

(def run-counts
  {:form-data-test 5})

(defspec form-data-extracted-correctly
  (:form-data-test run-counts)
  (prop/for-all
   [form-params (cljspec/gen ::specs/signup-form)]
   (cljspec/valid? ::specs/user-account
                   (user/create form-params))))
