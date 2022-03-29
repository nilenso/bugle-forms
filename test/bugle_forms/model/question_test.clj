(ns bugle-forms.model.question-test
  (:require
   [bugle-forms.db.connection :as db]
   [bugle-forms.factories :as factories]
   [bugle-forms.fixtures :as fixtures]
   [bugle-forms.model.form :as form]
   [bugle-forms.model.question :as sut]
   [bugle-forms.model.user :as user]
   [bugle-forms.specs :as specs]
   [clojure.spec.alpha :as s]
   [clojure.test :refer [deftest is testing use-fixtures]]
   [clojure.test.check.generators :as gen]
   [next.jdbc.sql :as sql]))

(use-fixtures :once fixtures/setup-config fixtures/setup-db)
(use-fixtures :each fixtures/clear-db)

(deftest add-question
  (testing "`create` creates a valid question"
    (let [question-text (gen/generate (s/gen :question/text))
          form-id (gen/generate (s/gen :question/form-id))
          question (sut/create question-text form-id)]
      (is (s/valid? ::specs/question question))))

  (testing "Questions are successfully inserted in database"
    (let [user (factories/user)
          {user-id :user-account/id} (user/insert! user)
          form (factories/form {:form/owner user-id})
          {form-id :form/id} (form/insert! form)
          questions (repeatedly 5 (partial factories/question
                                           {:question/form-id form-id}))
          _ (run! sut/insert! questions)]
      (is (every? not-empty
                  (map (partial sql/get-by-id db/datasource :question)
                       (:question/id questions)))))))

(deftest get-questions-of-form
  (testing "`get-questions` retrieves all the questions for a form"
    (let [user (factories/user)
          {user-id :user-account/id} (user/insert! user)
          form (factories/form {:form/owner user-id})
          {form-id :form/id} (form/insert! form)
          questions (repeatedly 5 (partial factories/question
                                           {:question/form-id form-id}))
          _ (run! sut/insert! questions)
          question-ids (map :question/id questions)
          retrieved-questions (sut/get-questions form-id)
          retrieved-question-ids (map :id retrieved-questions)]
      (is (= question-ids retrieved-question-ids))))

  (testing "`get-questions` for non-existent form returns empty collection"
    (let [non-existent-form-id (gen/generate gen/uuid)
          questions (sut/get-questions non-existent-form-id)]
      (is (empty? questions)))))

(deftest create-question-non-existent-form
  (testing "Cannot insert a question for a non-existent form"
    (let [question (factories/question)]
      (is (thrown? org.postgresql.util.PSQLException
                   (sut/insert! question))))))
