(ns schoolvillage.routes.home
  (:require [schoolvillage.db.core :as db]
             [schoolvillage.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :refer [ok]]
            [clojure.java.io :as io]))

(defn home-page []
  (layout/render
    "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn about-page []
  (layout/render "about.html"))

(defn edit-route [request]
  (let [user-id (get-in request [:params :user-id])]
    (layout/render "edit.html" {:endpoint (str "update/" user-id) :user (db/get-user 1)})))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page))
  (GET "/edit/:user-id" [] edit-route))
