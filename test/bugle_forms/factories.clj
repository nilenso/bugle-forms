(ns bugle-forms.factories
  (:require
   [bugle-forms.specs :as specs]
   [clojure.spec.alpha :as s]
   [clojure.spec.gen.alpha :as gen]))

(def create gen/generate)

(defn make-factory
  "Make factory for a given spec.
  The returned factory takes a map of overrides, which are checked against the
  spec."
  [spec]
  (fn factory-fn
    ([]
     (factory-fn {}))
    ([overrides]
     (s/gen spec
            (into {} (map (fn [[k v]]
                            {k (fn [] (gen/return v))})
                          overrides))))))

(defmacro make-factories
  "Create factory definitions from a list of factory declarations."
  [factory-decls]
  `(do ~@(map (fn [{:keys [name spec] :as _factory-decl}]
                `(def ~(symbol name)
                   ~(str "Returns generator for a " name ". "
                         "Optionally takes a map of overrides.")
                   (make-factory ~spec)))
              factory-decls)))

(make-factories [{:name "user"
                  :spec ::specs/user-account}
                 {:name "form"
                  :spec ::specs/form}
                 {:name "create-form-params"
                  :spec ::specs/form-creation-form}
                 {:name "question"
                  :spec ::specs/question}
                 {:name "question-form-params"
                  :spec ::specs/add-question-form}
                 {:name "answer"
                  :spec ::specs/answer}])
