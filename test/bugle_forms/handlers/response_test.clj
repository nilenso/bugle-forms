(ns bugle-forms.handlers.response-test
  (:require
   [bugle-forms.factories :as factories]
   [bugle-forms.fixtures :as fixtures]
   [bugle-forms.handlers.response :as sut]
   [bugle-forms.model.form :as form]
   [bugle-forms.model.question :as question]
   [bugle-forms.model.user :as user]
   [clojure.test :refer [deftest is testing use-fixtures]]
   [clojure.test.check.generators :as gen]))

(use-fixtures :once fixtures/setup-config fixtures/setup-db)
(use-fixtures :each fixtures/clear-db)

(deftest response-form
  (testing "Response submission form is correctly rendered for a given form"
    (let [user (factories/create (factories/user))
          {user-id :user-account/id} (user/insert! user)
          form (factories/create (factories/form {:form/owner user-id}))
          {form-id :form/id} (form/insert! form)
          questions (factories/create
                     (gen/vector (factories/question
                                  {:question/form-id form-id})))
          _ (run! question/insert! questions)
          request {:route-params {:form-id (str form-id)}
                   :session {:user {:name (:user/name user)
                                    :uuid user-id}}}
          response (sut/response-form request)]
      (is (= 200 (:status response)))))

  (testing "404 for a form that does not exist"
    (let [user (factories/create (factories/user))
          {user-id :user-account/id} (user/insert! user)
          non-existent-form (factories/create (factories/form))
          request {:route-params {:form-id (str (:form/id non-existent-form))}
                   :session {:user {:name (:user/name user)
                                    :uuid user-id}}}
          response (sut/response-form request)]
      (is (= 404 (:status response))))))
