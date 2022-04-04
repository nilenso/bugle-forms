(ns bugle-forms.handler-dispatch-test
  (:require
   [bugle-forms.handler-dispatch :as sut]
   [bugle-forms.routes :as routes]
   [clojure.test :refer [deftest is testing]]))

(deftest handler-from-spec
  (testing "returns a valid handler for some defined handler spec"
    (let [some-handler-key ::routes/home
          resultant-handler (sut/handler-from-spec some-handler-key)]
      (is (fn? resultant-handler))
      (is (= (get-in routes/handler-specs [some-handler-key
                                           :handler])
             resultant-handler))))

  (testing "returns an invalid handler for undefined handler spec"
    (let [undefined-handler-key ::routes/does-not-exist
          resultant-handler (sut/handler-from-spec undefined-handler-key)]
      (is (fn? resultant-handler))
      (is (= 500 (:status (resultant-handler {}))))))

  (testing "if handler key is function, returns input as-is"
    (let [fn-handler-key (constantly "hello")
          resultant-handler (sut/handler-from-spec fn-handler-key)]
      (is (fn? resultant-handler))
      (is (= fn-handler-key resultant-handler)))))
