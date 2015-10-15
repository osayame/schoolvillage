(ns schoolvillage.routes.dbadmin
  (:require [schoolvillage.db.core :as db :refer :all]
            [schoolvillage.layout :as layout]
            [compojure.core :refer :all]
            [ring.util.http-response :refer [ok]]
            [compojure.core :refer :all]
            [ring.middleware.basic-authentication :refer [wrap-basic-authentication]]
            [clojure.java.io :as io]
            [ring.util.response :as response]
            ))

(defn home-page []
  (layout/render
   "home.html"))

(defn edit-route [request]
  (let [user-id (get-in request [:params :id])]
    (layout/render "edit.html" {:endpoint (str "update/" user-id) :user (db/get-user 1)})))

(defn update-route [request]
  (let [user-id (get-in request [:params :id])
        first_name (get-in request [:params :first_name])
        last_name (get-in request [:params :last_name])
        email (get-in request [:params :email])
        phone (get-in request [:params :phone])
        ]
    (db/update-user user-id first_name last_name email phone)
    (response/redirect (str "/dbadmin/edit/" user-id))))

(defn new-route [request]
  (layout/render "edit.html" {:endpoint "add" :company {}}))

(defn add-route [request]
  (let [first_name (get-in request [:params :first_name])
        last_name (get-in request [:params :last_name])
        email (get-in request [:params :email])
        phone (get-in request [:params :phone])
        ]
    (db/insert-user<! {:id                               0
                    :first_name                       first_name
                    :last_name                        last_name
                    :email                            email
                    :phone                            phone
                    })
    (response/redirect "/dbadmin/")))

(defroutes dbadmin-routes
  (GET "/" [] (home-page))
  (GET "/edit/:id" [] edit-route)
  (POST "/update/:id" [] update-route)
  (GET "/new" [] new-route)
  (POST "/add" [] add-route)
  )

(defn admin? [name pass]
  (and (= name "osa")
       (= pass "osa")))

(defroutes admin-routes
  (context "/dbadmin" [] dbadmin-routes))

