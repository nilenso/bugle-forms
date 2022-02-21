(ns bugle-forms.handlers.utils
  (:require
   [hiccup.page :refer [html5]]
   [bugle-forms.views.home :as home-views]
   [bugle-forms.views.layout :as layout]
   [ring.util.response :as response]))

(defn home
  "Display home page."
  [{:keys [flash]}]
  (response/response (layout/application
                      {:title "Bugle Forms" :flash flash}
                      home-views/content)))

(defn not-found
  "Display 404 page."
  [_]
  (response/not-found "404. *sad bugle noises*"))

(defn bad-request
  "Display 400 bad request page."
  [_]
  (response/bad-request "Bad request."))

(defn error-redirect
  "Redirect to another route with a flash message.
  Use this to redirect to another page when something goes wrong."
  [route message]
  (-> (response/redirect route)
      (assoc :flash message)))