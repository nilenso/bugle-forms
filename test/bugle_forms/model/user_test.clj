(ns bugle-forms.model.user-test
  (:require
   [bugle-forms.fixtures :as fixtures]
   [bugle-forms.model.user :as user]
   [bugle-forms.specs :as specs]
   [clojure.spec.alpha :as cljspec]
   [clojure.test :refer [deftest is testing use-fixtures]]
   [clojure.test.check.clojure-test :refer [defspec]]
   [clojure.test.check.properties :as prop]))

(use-fixtures :once fixtures/setup-config fixtures/setup-db)
(use-fixtures :each fixtures/clear-db)

(def run-counts
  {:form-data-test 5})

(defspec form-data-extracted-correctly
  (:form-data-test run-counts)
  (prop/for-all
   [form-params (cljspec/gen ::specs/signup-form)]
   (cljspec/valid? ::specs/user-account
                   (user/create form-params))))

(deftest authenticate
  (let [user-data {:name "Ghanshyam Pinto"
                   :email "gs@nilen.so"
                   :password "t0ps3cr3t"}
        a-user (user/create user-data)
        _ (user/insert! a-user)]
    (testing "Existing user is assigned a valid session ID"
      (let [login-params (select-keys user-data [:email :password])
            auth-response (user/authenticate login-params)]
        (is (string? (:id auth-response)))
        (is (not-empty (:id auth-response)))
        (is (= (:name user-data) (:name auth-response)))))

    (testing "Nonexistent user gets an error"
      (let [login-params {:email "fake@fake.com" :password "t0ps3cr3t"}
            auth-response (user/authenticate login-params)]
        (is (= {:error :no-user-found} auth-response))))

    (testing "Wrong password gets an error"
      (let [login-params {:email (:email user-data) :password "hunter2"}
            auth-response (user/authenticate login-params)]
        (is (= {:error :invalid-password} auth-response))))))
