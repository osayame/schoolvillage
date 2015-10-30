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

(defn home-page [] (layout/render "home.html"))

(defn about-page [] (layout/render "about.html"))

(defn apply-page []
  (layout/render "apply.html" {:endpoint "submit" :context "submit" :subjects (db/get-all-subjects)}))

(defn thanks-page [] (layout/render "thanks.html"))

(defn book-page []
  (layout/render "book.html" {:subjects (db/get-all-subjects)}))

(defn sages-page [request]
  (let [subject (str (get-in request [:params :subject]))
        sages (db/get-sages-by-subject subject)]
    (layout/render "book_sages.html" {:sages sages :subject subject})))

(defn profile-page [request]
  (let [user (db/get-user-by-url (get-in request [:params :sage]))]
    (if (some? user)
      (layout/render "sage-profile.html" {:user user}))))

(defn add-tutor [request]
  (db/add-user (get-in request [:params]))
  (response/redirect "/thanks"))

(defroutes home-routes
  (route/resources "/")
  (POST "/submit" [] add-tutor)
  (GET "/" [] (home-page))
  (GET "/dbadmin" [] (response/redirect "/dbadmin/"))
  (GET "/about" [] (about-page))
  (GET "/apply" [] (apply-page))
  (GET "/book/:subject" [] sages-page)
  (GET "/book" [] (book-page))
  (GET "/thanks" [] (thanks-page))

  (GET "/:sage" [] profile-page)
  )
