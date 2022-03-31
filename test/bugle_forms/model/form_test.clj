(ns bugle-forms.model.form-test
  (:require
   [bugle-forms.db.connection :as db]
   [bugle-forms.factories :as factories]
   [bugle-forms.fixtures :as fixtures]
   [bugle-forms.model.form :as sut]
   [bugle-forms.model.user :as user]
   [bugle-forms.specs :as specs]
   [clojure.spec.alpha :as s]
   [clojure.test :refer [deftest is testing use-fixtures]]
   [clojure.test.check.generators :as gen]
   [next.jdbc.sql :as sql]))

(use-fixtures :once fixtures/setup-config fixtures/setup-db)
(use-fixtures :each fixtures/clear-db)

(deftest create-form
  (testing "`create` creates a valid form"
    (let [name (gen/generate (s/gen :form/name))
          user-id (gen/generate gen/uuid)
          form (sut/create name user-id)]
      (is (s/valid? ::specs/form form))))

  (testing "Form is successfully inserted in database"
    (let [user (factories/user)
          {user-id :user-account/id} (user/insert! user)
          form (factories/form {:form/owner user-id})
          {:form/keys [id]} (sut/insert! form)]
      (is (not-empty (sql/get-by-id db/datasource :form id))))))

(deftest get-form-data
  (testing "We can `get` an inserted form"
    (let [user (factories/user)
          {user-id :user-account/id} (user/insert! user)
          form (factories/form {:form/owner user-id})
          {:form/keys [id]} (sut/insert! form)
          inserted-form (-> (sql/get-by-id db/datasource :form id)
                            (update :form/status keyword))]
      (is (= (sut/get id) inserted-form))))

  (testing "Cannot `get` a form that does not exist"
    (let [non-existent-form (factories/form)
          non-existent-get-result (sut/get (:form/id non-existent-form))]
      (is (= :form-not-found (:error non-existent-get-result)))))

  (testing "All the user's forms are retrieved correctly"
    (let [user (factories/user)
          {user-id :user-account/id} (user/insert! user)
          forms (repeatedly 3 (partial factories/form {:form/owner user-id}))
          _ (mapv sut/insert! forms)
          retrieved-forms (sut/get-forms {:uuid user-id})
          expected (map #(select-keys % [:form/id :form/name :form/created])
                        (sort-by :form/created #(compare %2 %1) forms))]
      (is (= (:form/id expected) (:id retrieved-forms)))))

  (testing "`get-forms` for a user that does not exist returns empty collection"
    (let [uuid (gen/generate gen/uuid)]
      (is (empty? (sut/get-forms {:uuid uuid})))
      (is (empty? (sut/get-forms {:uuid nil}))))))

;; This is in a separate `deftest` because our fixtures lead to these tests
;; happening in a rollback-only transaction. Since this test throws an
;; exception, it causes subsequent tests to fail because the transaction fails,
;; and thus none of the other database commands are issued.
;;
;; Therefore, any tests throwing a database exception must be in its own
;; `deftest`
(deftest create-form-non-existent-user
  (testing "Cannot insert a form for a non-existent user"
    (let [form (factories/form)]
      (is (thrown? org.postgresql.util.PSQLException
                   (sut/insert! form))))))

(deftest publish-form
  (testing "We can change status of a draft form to published"
    (let [user (factories/user)
          {user-id :user-account/id} (user/insert! user)
          form (factories/form {:form/owner user-id
                                :form/status :draft})
          {:form/keys [id]} (sut/insert! form)
          publish-result (sut/publish! {:uuid user-id} id)
          published-form (sut/get id)]
      (is (not (:error publish-result)))
      (is (= :published (:form/status published-form)))))

  (testing "We cannot publish a form that's already published"
    (let [user (factories/user)
          {user-id :user-account/id} (user/insert! user)
          form (factories/form {:form/owner user-id})
          {:form/keys [id]} (sut/insert! form)
          _ (sut/publish! {:uuid user-id} id)
          republish-result (sut/publish! {:uuid user-id} id)]
      (is (= :form-already-published (:error republish-result)))))

  (testing "Error when trying to publish a form as a non-owner"
    (let [user (factories/user)
          {user-id :user-account/id} (user/insert! user)
          form (factories/form {:form/owner user-id
                                :form/status :draft})
          {:form/keys [id]} (sut/insert! form)
          unauthorized-user-id (gen/generate gen/uuid)
          unauthorized-publish-result (sut/publish! unauthorized-user-id id)]
      (is (= :unauthorized-access (:error unauthorized-publish-result)))))

  (testing "Error when trying to publish a non-existent form"
    (let [user (factories/user)
          {user-id :user-account/id} (user/insert! user)
          non-existent-form-id (gen/generate gen/uuid)
          result (sut/publish! user-id non-existent-form-id)]
      (is (= :form-not-found (:error result))))))

(deftest generate-form-link
  (testing "form link is generated for a valid form"
    (let [form (factories/form)
          expected-link (str "/form/" (:form/id form))
          actual-link (sut/link form)]
      (is expected-link actual-link)))

  (testing "form link is `nil` for invalid form"
    (let [form (gen/generate gen/any)
          generated-link (sut/link form)]
      (is (nil? generated-link))))

  (testing "form link is `nil` for unpublished form"
    (let [form (factories/form {:form/status :draft})
          generated-link (sut/link form)]
      (is (nil? generated-link)))))
