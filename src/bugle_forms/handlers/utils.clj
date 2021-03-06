(ns bugle-forms.handlers.utils
  (:require
   [hiccup.page :refer [html5]]
   [bugle-forms.views.home :as home-views]
   [bugle-forms.views.layout :as layout]
   [ring.util.response :as response]))

(defn home
  "Display home page."
  [{:keys [flash], {:keys [user]} :session}]
  (if user
    (response/redirect "/dashboard" :see-other)
    (response/response (layout/application
                        {:title "Bugle Forms" :flash flash}
                        home-views/content))))

(defn not-found
  "Display 404 page."
  [_]
  (response/not-found "404. *sad bugle noises*"))

(defn bad-request
  "Display 400 bad request page."
  [_]
  (response/bad-request "Bad request."))

(defn flash-redirect
  "Redirect to another route with a flash message."
  [route message]
  (-> (response/redirect route :see-other)
      (assoc :flash message)))

(defn error-response
  "Display 500 internal server error page."
  [_]
  (-> (response/response "500 Internal Server Error. *sad bugle noises*")
      (response/status 500)))
