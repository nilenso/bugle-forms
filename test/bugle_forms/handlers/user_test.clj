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
    (let [email "new-gs@nilen.so"
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
      (is (= 303 (:status response)))
      (is (= {"Location" "/signup"} (:headers response))))))

(deftest login-and-logout
  (let [signup-form-params {:name "Ghanshyam Pinto"
                            :email "gs@nilen.so"
                            :password "t0ps3cr3t"}
        signup-request {:form-params signup-form-params}
        _ (sut/create-user signup-request)]
    (testing "Successful login has session attached and redirects to dashboard"
      (let [login-form-params (select-keys signup-form-params
                                           [:email :password])
            login-request {:form-params login-form-params}
            response (sut/login login-request)]
        (is (:session response))
        (is (= 303 (:status response)))
        (is (= {"Location" "/dashboard"} (:headers response)))))

    (testing "Unsuccessful login redirects to login page again"
      (let [login-form-params {:email (:email signup-form-params)
                               :password "hunter2"}
            login-request {:form-params login-form-params}
            response (sut/login login-request)]
        (is (= 303 (:status response)))
        (is (nil? (:session response)))
        (is (= {"Location" "/login"} (:headers response)))))

    (testing "Logout deletes session"
      (let [login-form-params (select-keys signup-form-params
                                           [:email :password])
            login-request {:form-params login-form-params}
            {{{:keys [id]} :user} :session} (sut/login login-request)
            logout-request {:session {:name (:name signup-form-params)
                                      :id id}}
            response (sut/logout logout-request)]
        (is (nil? (:session response)))
        (is (= 303 (:status response)))
        (is (= {"Location" "/"} (:headers response)))))))
