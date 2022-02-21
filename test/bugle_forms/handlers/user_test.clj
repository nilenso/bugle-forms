(ns bugle-forms.handlers.user-test
  (:require
   [bugle-forms.model.user :as user]
   [bugle-forms.handlers.user :as sut]
   [bugle-forms.fixtures :as fixtures]
   [clojure.test :refer [deftest is testing use-fixtures]]))

(use-fixtures :once fixtures/setup-config fixtures/setup-db)
(use-fixtures :each fixtures/clear-db)

(deftest create-user
  (testing "User is created successfully"
    (let [email "gs@nilen.so"
          signup-form-params {:name "Ghanshyam"
                              :email email
                              :password "t0ps3cr3t"}
          request {:form-params signup-form-params}
          user (user/create signup-form-params)
          response (sut/create-user request)]
      (is (= 303 (:status response)))
      (is (= {"Location" "/login"} (:headers response)))
      (is (user/in-database? user))))

  (testing "Cannot signup if already signed up with a particular email ID"
    (let [email "gs@nilen.so"
          signup-form-params {:name "Ghanshyam Pinto"
                              :email email
                              :password "t0ps3cr3t"}
          request {:form-params signup-form-params}
          response (sut/create-user request)]
      (is (= 302 (:status response)))
      (is (= {"Location" "/signup"} (:headers response))))))
