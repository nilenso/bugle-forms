(ns bugle-forms.views.layout
  (:require [hiccup.page :refer [include-css html5]]))

(defn- navbar
  "Return a navbar representation from pairs of link titles and locations."
  [nav-items]
  [:div {:class "nav"}
   [:a {:href "/" :title "home"} "🎺"]
   (map (fn [{:keys [title url]}]
          [:a {:href url :title title} title])
        nav-items)])

(defn application
  "Returns a representation of the application frontend.
  Generates HTML based on a title, a flash message that is displayed to the user
  when specified, and user information, if they are logged in."
  ([{:keys [title flash user]} & content]
   (html5 [:head [:title title]
           [:meta {:content "text/html" :charset "utf-8"}]
           [:link {:rel "apple-touch-icon" :type "image/png" :sizes "180x180" :href "/public/apple-touch-icon.png"}]
           [:link {:rel "icon" :type "image/png" :sizes "16x16" :href "/public/favicon-16x16.png"}]
           [:link {:rel "icon" :type "image/png" :sizes "32x32" :href "/public/favicon-32x32.png"}]
           [:link {:rel "manifest" :href "/public/site.webmanifest"}]
           (include-css "/public/css/main.css")]
          [:body
           [:div {:class "main"}
            (if user
              (navbar [{:title "Dashboard" , :url "/dashboard"}
                       {:title "Log Out" , :url "/logout"}])
              (navbar [{:title "Log In", :url "/login"}
                       {:title "Sign Up", :url "/signup"}]))
            [:div {:class "content"}
             (when flash
               [:div {:class "flash-message"} flash])
             [:h1 {:class "page-title"} title]
             [:div {:class "page-content"} content]]]])))
