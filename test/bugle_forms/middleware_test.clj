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
