(ns bugle-forms.handlers.core
  (:require
   [hiccup.page :refer [html5]]
   [bugle-forms.views.home :as home-views]
   [bugle-forms.views.layout :as layout]
   [ring.util.response :as response]))

(defn home
  "Display home page."
  [{:keys [flash]}]
  (response/response
   (html5 (layout/application {:title "Bugle Forms" :flash flash}
                              home-views/content))))

(defn not-found
  "Display 404 page."
  [_]
  (response/response "404. *sad bugle noises*"))
