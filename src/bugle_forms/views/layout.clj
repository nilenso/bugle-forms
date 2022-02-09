(ns bugle-forms.views.layout
  (:require [hiccup.page :refer [include-css]]))

(defn- navbar [nav-items]
  [:div {:class "nav"}
   [:a {:href "/" :title "home"} "🎺"]
   (map (fn [[link-title link-url]]
          [:a {:href link-url :title link-title} link-title])
        nav-items)])

(defn application [title & content]
  (list [:head [:title title]
         [:meta {:content "text/html" :charset "utf-8"}]
         [:link {:rel "apple-touch-icon" :type "image/png" :sizes "180x180" :href "/public/apple-touch-icon.png"}]
         [:link {:rel "icon" :type "image/png" :sizes "16x16" :href "/public/favicon-16x16.png"}]
         [:link {:rel "icon" :type "image/png" :sizes "32x32" :href "/public/favicon-32x32.png"}]
         [:link {:rel "manifest" :href "/public/site.webmanifest"}]
         (include-css "/public/css/main.css")]
        [:body
         [:div {:class "main"}
          (navbar [["Log In" "/login"]
                   ["Sign Up" "/signup"]])
          [:div {:class "content"}
           [:h1 {:class "page-title"} title]
           [:div {:class "page-content"} content]]]]))
