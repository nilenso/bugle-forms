(ns bugle-forms.middleware-test
  (:require [bugle-forms.middleware :as m]
            [clojure.java.io :as io]
            [clojure.test :refer [deftest is testing]]))

(deftest uncaught-exceptions-return-error-response
  (testing "handler wrapped in wrap-exception-handler returns 500"
    (let [handler-with-error (fn [_]
                               (throw (Exception. "This is an unexpected error!")))
          request            {:some "request map"}
          app                (m/wrap-exception-handler handler-with-error)
          response           (with-open [the-void (io/writer "/dev/null")]
                               (binding [*out* the-void]
                                 (app request)))]
      (is (= (:status response) 500)))))

(deftest keyword-form-params-keywordizes-form-params
  (testing "wrap-keyword-form-params keywordizes :form-params"
    (let [request-with-str-form-params {:form-params
                                        {"name"  "Ghanshyam"
                                         "email" "gs@foo.com"}}
          app                          (m/wrap-keyword-form-params identity)
          response                     (app request-with-str-form-params)]
      (is (every? keyword? (keys (:form-params response)))))))

(deftest validate-request
  (testing "malformed requests return a 400 Bad Request response"
    (let [handler (fn [_] {:status 200, :headers {}, :body "Success"})
          wrapped-handler (m/validate-request handler
                                              {:spec even?
                                               :field :params})
          malformed-request {:request-method :get
                             :params 3}
          response (wrapped-handler malformed-request)]
      (is (= (:status response) 400))))

  (testing "well-formed requests return the handler's response"
    (let [handler (fn [_] {:status 200, :headers {}, :body "Success"})
          wrapped-handler (m/validate-request handler
                                              {:spec even?
                                               :field :params})
          request {:request-method :get
                   :params 2}
          response (wrapped-handler request)]
      (is (= (:status response) 200))
      (is (= (:body response) "Success")))))

(deftest wrap-access-control
  (testing "Logged in user trying to access a guest resource is redirected to dashboard"
    (let [handler (fn [_] {:status 200, :headers {}, :body "Log in page"})
          wrapped-handler (m/wrap-access-control handler {:needs :guest})
          session-request {:request-method :get
                           :session {:user {:name "Ghanshyam"
                                            :id (java.util.UUID/randomUUID)}}}
          response (wrapped-handler session-request)]
      (is (= (:status response) 303))
      (is (= (:headers response) {"Location" "/dashboard"}))))

  (testing "Logged in user has access to a member resource"
    (let [handler (fn [_] {:status 200, :headers {}, :body "Dashboard"})
          wrapped-handler (m/wrap-access-control handler {:needs :member})
          session-request {:request-method :get
                           :session {:user {:name "Ghanshyam"
                                            :id (java.util.UUID/randomUUID)}}}
          response (wrapped-handler session-request)]
      (is (= (:status response) 200))
      (is (= (:body response) "Dashboard"))))

  (testing "Logged out user trying to access a member resource is redirected to login"
    (let [handler (fn [_] {:status 200, :headers {}, :body "Dashboard"})
          wrapped-handler (m/wrap-access-control handler {:needs :member})
          session-request {:request-method :get}
          response (wrapped-handler session-request)]
      (is (= (:status response) 303))
      (is (= (:flash response) "You need to log in to view this resource."))
      (is (= (:headers response) {"Location" "/login"}))))

  (testing "Logged out user has access to a guest resource"
    (let [handler (fn [_] {:status 200, :headers {}, :body "Log in page"})
          wrapped-handler (m/wrap-access-control handler {:needs :guest})
          session-request {:request-method :get}
          response (wrapped-handler session-request)]
      (is (= (:status response) 200))
      (is (= (:body response) "Log in page")))))
