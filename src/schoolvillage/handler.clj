(ns schoolvillage.handler
  (:require
   [compojure.core :refer [defroutes routes wrap-routes]]

   [ring.middleware.reload :as reload]
   [ring.middleware.json :as middleware-json]
   [ring.middleware.file-info :refer [wrap-file-info]]
   [ring.middleware.keyword-params :refer [wrap-keyword-params]]
   [ring.middleware.params :refer [wrap-params]]
   [ring.middleware.nested-params :refer [wrap-nested-params]]
   [ring.middleware.session.cookie :refer :all]
   [ring.middleware.basic-authentication :refer [wrap-basic-authentication]]
   [ring.util.response :refer [redirect]]
   [ring.middleware.session :refer [wrap-session]]


   [schoolvillage.layout :refer [error-page]]
   [schoolvillage.routes.home :refer [home-routes]]
   [schoolvillage.routes.dbadmin :refer [admin-routes]]
   [schoolvillage.middleware :as middleware]
   [schoolvillage.db.core :as db]

   [compojure.route :as route]
   [taoensso.timbre :as timbre]
   [environ.core :refer [env]]

   [buddy.auth.middleware :refer [wrap-authentication ]]
   [buddy.auth.backends.session :refer [session-backend]]
   [buddy.auth.accessrules :refer [restrict wrap-access-rules]]
   [buddy.auth :refer [authenticated?]]

   [taoensso.timbre :refer :all]
   [taoensso.timbre.appenders.3rd-party.rotor :as rotor]

   [selmer.parser :as parser]

   [org.httpkit.server :as server]))

(defn init
  "init will be called once when
  app is deployed as a servlet on
  an app server such as Tomcat
  put any initialization code here"
  []

  (timbre/merge-config!
   {:level     (if (env :dev) :trace :info)
    :appenders {:rotor (rotor/rotor-appender
                        {:path "schoolvillage.log"
                         :max-size (* 512 1024)
                         :backlog 10})}})

  (if (env :dev) (parser/cache-off!))
  (db/connect!)
  (timbre/info (str
                "\n-=[schoolvillage started successfully"
                (when (env :dev) " using the development profile")
                "]=-")))

(defn destroy
  "destroy will be called when your application
  shuts down, put any clean up code here"
  []
  (timbre/info "schoolvillage is shutting down...")
  (db/disconnect!)
  (timbre/info "shutdown complete!"))

(def app (-> (apply routes [home-routes admin-routes])
             (wrap-authentication (session-backend))
             (wrap-session {:cookie-name "schoolvillage"
                            :cookie-attrs {:max-age (* 60 60 24)}
                            :store (cookie-store {:key (:cookie-key "aaaabbbbccccdddd")})})
             wrap-file-info
             wrap-keyword-params
             middleware-json/wrap-json-body
             middleware-json/wrap-json-response
             middleware-json/wrap-json-params
             wrap-nested-params
             wrap-params))
