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

(defn misc-page []
  (layout/render "misc.html" {:user (db/get-user {:id 1})}))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page))
  (GET "/misc" [] (misc-page)))
