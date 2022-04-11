(ns bugle-forms.model.answer-test
  (:require
   [bugle-forms.model.answer :as sut]
   [bugle-forms.specs :as specs]
   [clojure.spec.alpha :as s]
   [clojure.test.check.clojure-test :refer [defspec]]
   [clojure.test.check.properties :as prop]))

(defspec create-answer-creates-valid-answer
  10
  (prop/for-all
   [response-id (s/gen :answer/response-id)
    question-id (s/gen :answer/question-id)
    text (s/gen :answer/text)]
   (s/valid? ::specs/answer (sut/create response-id question-id text))))
