(ns bugle-forms.routes-test
  (:require
   [bidi.bidi :as bidi]
   [bugle-forms.routes :as sut]
   [clojure.set :as cljset]
   [clojure.test :refer [deftest is testing]]))

(deftest routes-correctly-match
  (testing "all handler entities dispatch to a handler"
    (let [all-handler-routes (->> (bidi/route-seq sut/routes)
                                  (map :handler)
                                  (filter keyword?)
                                  set)
          dispatch-routes (set (keys sut/handler-specs))]
      (is (= all-handler-routes dispatch-routes)))))

(deftest access-control-definitions
  (testing "All routes have correctly defined access"
    (let [expected-guest-routes #{::sut/signup
                                  ::sut/login-form
                                  ::sut/login}
          expected-member-routes #{::sut/logout
                                   ::sut/dashboard
                                   ::sut/form-builder
                                   ::sut/add-question
                                   ::sut/create-form
                                   ::sut/publish-form
                                   ::sut/response-form}
          all-access-routes #{::sut/home
                              ::sut/create-user
                              ::sut/create-response
                              ::sut/not-found}
          all-routes (cljset/union
                      expected-guest-routes
                      expected-member-routes
                      all-access-routes)
          guest-matches (map #(get sut/handler-specs %)
                             expected-guest-routes)
          member-matches (map #(get sut/handler-specs %)
                              expected-member-routes)
          all-access-matches (map #(get sut/handler-specs %)
                                  all-access-routes)]
      (is (= all-routes (set (keys sut/handler-specs))))
      (is (every? (partial = :guest)
                  (map #(get-in % [:access-control :needs])
                       guest-matches)))
      (is (every? (partial = :member)
                  (map #(get-in % [:access-control :needs])
                       member-matches)))
      (is (every? nil? (map #(get-in % [:access-control :needs])
                            all-access-matches))))))
