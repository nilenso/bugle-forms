(ns bugle-forms.handlers.core
  (:require
   [hiccup.page :refer [html5]]
   [bugle-forms.views.home :as home-views]
   [bugle-forms.views.layout :as layout]
   [ring.util.response :as response]))

(defn home [_]
  (response/response
   (html5 (layout/application "Bugle Forms" home-views/content))))

(defn not-found [_]
  (response/response "404. *sad bugle noises*"))
