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
    (if (some? user)
      (layout/render "sage-profile.html" {:user user}))))

(defn about-page []
  (layout/render "about.html"))

(defn apply-page []
  (layout/render "apply.html" {:endpoint "add" :user {}}))

(defn add-tutor [request]
  (db/add-user2 (get-in request [:params]))
  (response/redirect (str "/")))

(defroutes home-routes
  (route/resources "/")
  (GET "/" [] (home-page))
  (GET "/dbadmin" [] (response/redirect "/dbadmin/"))
  (GET "/about" [] (about-page))
  (GET "/apply" [] (apply-page))
  (GET "/:sage" [] profile-page)
  (POST "/add" [] add-tutor)
  )
