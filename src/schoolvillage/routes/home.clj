(ns schoolvillage.routes.home
  (:require [schoolvillage.db.core :as db :refer :all]
            [schoolvillage.layout :as layout]
            [schoolvillage.utilities :refer :all]
            [compojure.route :as route]
            [ring.util.http-response :refer [ok]]
            [compojure.core :refer :all]
            [clojure.java.io :as io]
            [ring.util.response :as response]
            ))

(defn home-page [] (layout/render "home.html"))

(defn about-page [] (layout/render "about.html"))

(defn apply-page []
  (layout/render "apply.html" {:endpoint "submit"
                               :context "submit"
                               :subjects (db/get-all-subjects)}))

(defn thanks-page [] (layout/render "thanks.html"))

(defn book-page []
  (layout/render "zipcode.html" {:endpoint "zipcode"}))

(defn subjects-page [request]
  (layout/render "book_subjects.html" {:subjects (db/get-all-subjects)}))

;;(defn sages-page [request]
  ;;(let [subject (str (get-in request [:params :subject]))
    ;;    zipcode (str (get-in request [:session :zipcode]))
      ;;  sages (db/get-sages-by-subject subject zipcode)]
    ;;(layout/render "book_sages.html" {:sages sages :subject subject})))

(defn sages-page [request]
  (let [subject (str (get-in request [:params :subject]))
        zipcode (str (get-in request [:session :zipcode]))]
    (layout/render "book_sages.html" {:sages {} :subject subject})))

(defn profile-page [request]
  (let [user (db/get-user-by-url (get-in request [:params :sage]))]
    (if (some? user)
      (layout/render "sage-profile.html" {:user user}))))

(defn add-tutor [request]
  (try
    (db/add-user (get-in request [:params]))
    (finally (response/redirect "/"))))

(defn get-state-route [request]
  (str (:state
        (db/get-user
         (Integer. (get-in request [:params :id]))))))

(defn zipcode-route [request]
  (let [zipcode (get-in request [:params :zipcode])]
    (assoc (response/redirect "/subjects")
      :session (assoc (request :session) :zipcode zipcode))))

(defroutes home-routes
  (route/resources "/")
  (POST "/submit" [] add-tutor)
  (POST "/zipcode" [] zipcode-route)
  (POST "/subscribe" {params :params}
    (subscribe-mailchimp (:first_name params) (:last_name params) (:email params) "4debbe9582" "Inquiries")
    (response/redirect "/"))
  (GET "/" [] (home-page))
  (GET "/dbadmin" [] (response/redirect "/dbadmin/"))
  (GET "/about" [] (about-page))
  (GET "/apply" [] (apply-page))
  (GET "/subjects" [] subjects-page)

  (GET "/book/:subject" [] sages-page)
  (GET "/book" [] (book-page))
  (GET "/thanks" [] (thanks-page))
  (GET "/:sage" [] profile-page)
  (GET "/state/:id" [] get-state-route)

  )
