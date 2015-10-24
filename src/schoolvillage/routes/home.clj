(ns schoolvillage.routes.home
  (:require [schoolvillage.db.core :as db :refer :all]
            [schoolvillage.layout :as layout]

            [compojure.core :refer :all]
            [compojure.route :as route]

            [ring.util.http-response :refer [ok]]
            [compojure.core :refer :all]
            [clojure.java.io :as io]
            [ring.util.response :as response]
            ))

(defn home-page []
  (layout/render "home.html"))

(defn profile-page [request]
  (let [user (db/get-user-by-url (get-in request [:params :sage]))]
    (if (nil? user)
      (layout/render "404-error.html")
      (layout/render "sage-profile.html" {:user user}))))

(defn about-page []
  (layout/render "about.html"))

(defroutes home-routes
  (route/resources "/")
  (GET "/" [] (home-page))
  (GET "/dbadmin" [] (response/redirect "/dbadmin/"))
  (GET "/about" [] (about-page))
  (GET "/:sage" [] profile-page)
  )
