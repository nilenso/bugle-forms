(ns bugle-forms.handlers.user
  (:require
   [bugle-forms.views.user :as user-views]
   [bugle-forms.views.layout :as layout]
   [hiccup.page :refer [html5]]
   [ring.util.response :as response]))

(defn login [_]
  (response/response "Stub for log-in"))

(defn signup [_]
  (response/response
   (html5 (layout/application "Sign up" user-views/signup))))
