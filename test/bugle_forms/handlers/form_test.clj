(ns bugle-forms.handlers.form-test
  (:require
   [bugle-forms.db.connection :as db]
   [bugle-forms.factories :as factories]
   [bugle-forms.fixtures :as fixtures]
   [bugle-forms.handlers.form :as sut]
   [bugle-forms.model.form :as form]
   [bugle-forms.model.user :as user]
   [clojure.test :refer [deftest is testing use-fixtures]]
   [next.jdbc.sql :as sql]))

(use-fixtures :once fixtures/setup-config fixtures/setup-db)
(use-fixtures :each fixtures/clear-db)

(deftest create-form
  (testing "Form creation handler is successfully creating a form"
    (let [user (factories/user)
          {:user-account/keys [id]} (user/insert! user)
          {form-name :name :as form-params} (factories/create-form-params)
          request {:form-params form-params
                   :session {:user {:name (:user/name user)
                                    :uuid id}}}
          response (sut/create request)]
      (is (= 303 (:status response)))
      (is (.startsWith (get (:headers response) "Location") "/form-builder/"))
      (is (not-empty (sql/find-by-keys db/datasource :form
                                       {:name form-name
                                        :owner id}))))))

(deftest form-builder
  (testing "Form builder is correctly rendered for a particular form"
    (let [user (factories/user)
          {user-id :user-account/id} (user/insert! user)
          form (factories/form {:form/owner user-id})
          {form-id :form/id} (form/insert! form)
          request {:route-params {:id (.toString form-id)}
                   :session {:user {:name (:user/name user)
                                    :uuid user-id}}}
          response (sut/form-builder request)]
      (is (= 200 (:status response)))))

  (testing "404 when accessing a form that does not exist"
    (let [user (factories/user)
          {user-id :user-account/id} (user/insert! user)
          non-existent-form (factories/form)
          request {:route-params {:id (str (:form/id non-existent-form))}
                   :session {:user {:name (:user/name user)
                                    :uuid user-id}}}
          response (sut/form-builder request)]
      (is (= 404 (:status response))))))

(deftest publish-form
  (testing "Publishing a form redirects to dashboard"
    (let [user (factories/user)
          {user-id :user-account/id} (user/insert! user)
          form (factories/form {:form/owner user-id
                                :form/status :draft})
          {form-id :form/id} (form/insert! form)
          request {:route-params {:form-id (str form-id)}
                   :session {:user {:name (:user/name user)
                                    :uuid user-id}}}
          response (sut/publish request)]
      (is (= 303 (:status response)))
      (is (= {"Location" "/dashboard"} (:headers response))))))
