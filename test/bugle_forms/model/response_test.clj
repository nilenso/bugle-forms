(ns bugle-forms.model.response-test
  (:require
   [bugle-forms.db.connection :as db]
   [bugle-forms.factories :as factories]
   [bugle-forms.fixtures :as fixtures]
   [bugle-forms.model.form :as form]
   [bugle-forms.model.response :as sut]
   [bugle-forms.model.question :as question]
   [bugle-forms.model.user :as user]
   [bugle-forms.specs :as specs]
   [clojure.spec.alpha :as s]
   [clojure.test :refer [deftest is testing use-fixtures]]
   [clojure.test.check.clojure-test :refer [defspec]]
   [clojure.test.check.generators :as gen]
   [clojure.test.check.properties :as prop]
   [next.jdbc.sql :as sql]))

(use-fixtures :once fixtures/setup-config fixtures/setup-db)
(use-fixtures :each fixtures/clear-db)

(defspec create-answers-creates-valid-answers
  10
  (prop/for-all
   [form-params (factories/response-form-params)
    response (factories/response)]
   (s/valid? (s/coll-of ::specs/answer)
             (sut/create-answers form-params response))))

(defspec create-response-creates-valid-response
  10
  (prop/for-all
   [form (factories/form)]
   (s/valid? ::specs/response
             (sut/create form))))

(deftest insert-response
  (testing "Responses are correctly inserted into the database"
    (let [user (factories/create (factories/user))
          {user-id :user-account/id :as _inserted-user} (user/insert! user)
          {form-id :form/id :as form} (factories/create
                                       (factories/form
                                        {:form/owner user-id}))
          _ (form/insert! form)
          {response-id :response/id :as response} (factories/create
                                                   (factories/response
                                                    {:response/form-id
                                                     form-id}))
          _ (sut/insert! response)
          retrieved-response (sql/get-by-id db/datasource
                                            :response response-id)]
      (is (= response retrieved-response)))))

(deftest insert-answers
  (testing "Answers are correctly inserted into the database"
    (let [user (factories/create (factories/user))
          {user-id :user-account/id :as _inserted-user} (user/insert! user)
          {form-id :form/id :as form} (factories/create
                                       (factories/form
                                        {:form/owner user-id}))
          _ (form/insert! form)
          questions (factories/create
                     (gen/vector (factories/question
                                  {:question/form-id form-id})))
          inserted-questions (mapv question/insert! questions)
          {response-id :response/id :as response} (factories/create
                                                   (factories/response
                                                    {:response/form-id
                                                     form-id}))
          _ (sut/insert! response)
          question-ids (map :question/id inserted-questions)
          answers (factories/create
                   (gen/vector (gen/fmap
                                #(assoc % :answer/question-id
                                        (rand-nth question-ids))
                                (factories/answer
                                 {:answer/response-id
                                  response-id}))))
          _ (sut/insert-answers! answers)
          retrieved-answers (sql/query db/datasource
                                       ["select * from answer
                                         where response_id = ?"
                                        response-id])]
      (is (= answers retrieved-answers)))))

(deftest create-and-store
  (testing "Response and its contained answers are stored correctly"
    (let [user (factories/create (factories/user))
          {user-id :user-account/id :as _inserted-user} (user/insert! user)
          {form-id :form/id :as form} (factories/create
                                       (factories/form
                                        {:form/owner user-id}))
          _ (form/insert! form)
          questions (factories/create
                     (gen/vector (factories/question
                                  {:question/form-id form-id})))
          inserted-questions (mapv question/insert! questions)
          question-ids (map (comp str :question/id) inserted-questions)
          form-params (zipmap question-ids (factories/create
                                            (gen/vector (s/gen :answer/text)
                                                        (count question-ids))))
          question-ids (keys form-params)
          {:keys [response-id]} (sut/create-and-store! form-params form)
          retrieved-response (sql/get-by-id db/datasource :response response-id)
          retrieved-answers (sql/query db/datasource
                                       ["select * from answer
                                         where response_id = ?"
                                        response-id])
          retrieved-answer-ids (map (comp str :answer/question-id) retrieved-answers)
          retrieved-answer-response-ids (map :answer/response-id
                                             retrieved-answers)]
      (is (not-empty retrieved-response))
      (is (= question-ids retrieved-answer-ids))
      (is (apply = (conj retrieved-answer-response-ids
                         response-id))))))
