(ns <<ns-name>>.web.pages.layout
  (:require
   [clojure.java.io]
   [hiccup.page :refer [html5 include-css]]
   [ring.util.http-response :refer [content-type ok]]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
   [ring.util.response]))

(defn base
  [title & content]
  (html5
   [:head
    [:title title]
    [:meta {:charset "utf-8"}]
    [:meta {:name    "viewport"
            :content "width=device-width, initial-scale=1.0"}]
    (include-css "css/screen.css")]
   [:body
    content<% if cljs? %>

    [:div#app "Loading..."]
    [:script {:src "js/app.js"}] <% endif %>]))
