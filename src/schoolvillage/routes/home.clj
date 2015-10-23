(ns schoolvillage.routes.home
  (:require [schoolvillage.db.core :as db :refer :all]
            [schoolvillage.layout :as layout]

            [compojure.core :refer :all]
            [compojure.route :as route]

            [ring.util.http-response :refer [ok]]
            [compojure.core :refer :all]
            [clojure.java.io :as io]))

(defn home-page []
  (layout/render "home.html" {:user (db/get-user 20)}))

(defn profile-page []
  (layout/render "sage-profile.html" {:user (db/get-user 20)}))

(defn about-page []
  (layout/render "about-us.html"))

(defroutes home-routes
  (route/resources "/")
  (GET "/" [] (home-page))
  (GET "/ogaius" [] (profile-page))
  (GET "/about" [] (about-page)))
