(ns bugle-forms.handlers.question-test
  (:require
   [bugle-forms.db.connection :as db]
   [bugle-forms.factories :as factories]
   [bugle-forms.fixtures :as fixtures]
   [bugle-forms.handlers.question :as sut]
   [bugle-forms.model.question :as question]
   [bugle-forms.model.form :as form]
   [bugle-forms.model.user :as user]
   [clojure.test :refer [deftest is testing use-fixtures]]
   [next.jdbc.sql :as sql]))

(use-fixtures :once fixtures/setup-config fixtures/setup-db)
(use-fixtures :each fixtures/clear-db)

(deftest add-question
  (testing "Add question handler successfully adds a question to form"
    (let [user (factories/user)
          {user-id :user-account/id} (user/insert! user)
          form (factories/form {:form/owner user-id})
          {form-id :form/id} (form/insert! form)
          {question-text :text :as form-params}
          (factories/question-form-params)
          request {:form-params form-params
                   :route-params {:form-id (str form-id)}
                   :session {:user {:name (:user/name user)
                                    :uuid user-id}}}
          response (sut/add-question request)]
      (is (= 303 (:status response)))
      (is (.startsWith (get (:headers response) "Location") "/form-builder/"))
      (is (not-empty (sql/find-by-keys db/datasource :question
                                       {:text question-text
                                        :form-id form-id}))))))
