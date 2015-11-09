(ns schoolvillage.routes.dbadmin
  (:require [schoolvillage.db.core :as db :refer :all]
            [schoolvillage.layout :as layout]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.http-response :refer [ok]]
            [compojure.core :refer :all]
            [ring.middleware.basic-authentication :refer [wrap-basic-authentication]]
            [clojure.java.io :as io]
            [ring.util.response :as response :refer :all]
            ))

(defn redirect-home [] (redirect (str "/dbadmin/")))

(defn approved?-for-subject [user subject]
  (.contains (:subjects user) (:id subject)))

(defn home-page []
  (println "routing to dbadmin home")
  (layout/render "dbadmin/home.html" {:flagged (db/get-flagged-users)
                                      :pending (db/get-pending-users)
                                      :recent (db/get-recent-users)}))

(defn edit-route [request]
  (let [user (db/get-user (get-in request [:params :id]))]
    (layout/render "dbadmin/edit.html" {:endpoint "dbadmin/update"
                                        :context "update"
                                        :id (:id user)
                                        :subjects (map #(assoc % :approved (approved?-for-subject user %)) (db/get-all-subjects))
                                        :user user}
                                        )))

(defn update-route [request]
  (db/update-user (get-in request [:params]))
  (redirect (str "/dbadmin/edit/" (get-in request [:params :id]))))

(defn add-route [request]
  (db/add-user (get-in request [:params]))
  (redirect-home))

(defn new-route [request]
  (layout/render "dbadmin/edit.html" {:endpoint "dbadmin/add"
                                      :context "add"
                                      :subjects (db/get-all-subjects)}))

(defn approve-route [request]
  (let [user-id (get-in request [:params :id])]
    (db/set-new-status (Integer. user-id) "Approved"))
  (redirect-home))

(defn sages-route [request]
  (layout/render "dbadmin/sages.html" {:sages (db/get-all-users)}))

(defn get-state-route [request]
  (str (:state
        (db/get-user
         (Integer. (get-in request [:params :id]))))))

(defroutes dbadmin-routes
  (GET "/" [] (home-page))
  (GET "/edit/:id" [] edit-route)
  (POST "/update/:id" [] update-route)
  (GET "/new" [] new-route)
  (POST "/add" [] add-route)
  (GET "/approve/:id" [] edit-route)
  (POST "/approve/:id" [] approve-route)
  (GET "/sages" [] sages-route)
  (GET "/state/:id" [] get-state-route)
  )

(defn admin? [name pass]
  (and (= name "osa")
       (= pass "osa")))

(defroutes admin-routes
  (context "/dbadmin" [] dbadmin-routes)
  )
