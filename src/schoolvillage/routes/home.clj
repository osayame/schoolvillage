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
  (layout/render "apply.html" {:endpoint "submit" :context "submit" :subjects (db/get-all-subjects)}))

(defn book-page []
  (layout/render "book.html" {:subjects (db/get-all-subjects)}))

(defn add-tutor [request]
  (println "!!!!!!!!!!!!!!!!!!!!!!!!!!! submit route"))

(defn thanks-page []
  (layout/render "thanks.html"))

(defn thanks-page []
  (layout/render "thanks.html"))

(defroutes home-routes
  (route/resources "/")
  (POST "/submit" [] add-tutor)
  (GET "/" [] (home-page))
  (GET "/dbadmin" [] (response/redirect "/dbadmin/"))
  (GET "/about" [] (about-page))
  (GET "/book" [] (book-page))
  (GET "/book/:subject" [] book-page)
  (GET "/apply" [] (apply-page))
  (GET "/thanks" [] (thanks-page))
  (POST "/submit" [] add-tutor)
  (GET "/:sage" [] profile-page)
  )

