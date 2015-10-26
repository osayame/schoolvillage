(ns schoolvillage.db.core
  (:require
   [cheshire.core :refer [generate-string parse-string]]
   [clojure.java.jdbc :as jdbc]
   [conman.core :as conman]
   [environ.core :refer [env]])
  (:import org.postgresql.util.PGobject
           org.postgresql.jdbc4.Jdbc4Array
           clojure.lang.IPersistentMap
           clojure.lang.IPersistentVector
           [java.sql
            BatchUpdateException
            Date
            Timestamp
            PreparedStatement]))

(defonce ^:dynamic *conn* (atom nil))

(conman/bind-connection *conn* "sql/queries.sql")

(defn get-user [id]
  (first (select-user {:id (Integer. id)})))

(defn update-user [params]
  (update-user<! (assoc params :id (Integer. (:id params)))))

(defn add-user [params]
  (insert-user<! (assoc params :id 0)))

(defn get-all-subjects [] (select-subjects))

(defn get-all-users [] (select-all-users))

(defn get-flagged-users [] (select-flagged-users))

(defn get-pending-users [] (select-pending-users))

(defn get-recent-users [] (select-recent-users))

(defn get-user-by-url [url]
  (first (select-user-by-url {:url (str url)})))

(defn set-new-status [id status]
  (update-status<! {:id id :status (str status)}))

(def pool-spec
  {:adapter    :postgresql
   :init-size  1
   :min-idle   1
   :max-idle   4
   :max-active 32})

(defn connect! []
  (conman/connect!
   *conn*
   (assoc
     pool-spec
     :jdbc-url (env :database-url))))

(defn disconnect! []
  (conman/disconnect! *conn*))

(defn to-date [sql-date]
  (-> sql-date (.getTime) (java.util.Date.)))

(extend-protocol jdbc/IResultSetReadColumn
  Date
  (result-set-read-column [v _ _] (to-date v))

  Timestamp
  (result-set-read-column [v _ _] (to-date v))

  Jdbc4Array
  (result-set-read-column [v _ _] (vec (.getArray v)))

  PGobject
  (result-set-read-column [pgobj _metadata _index]
                          (let [type  (.getType pgobj)
                                value (.getValue pgobj)]
                            (case type
                              "json" (parse-string value true)
                              "jsonb" (parse-string value true)
                              "citext" (str value)
                              value))))

(extend-type java.util.Date
  jdbc/ISQLParameter
  (set-parameter [v ^PreparedStatement stmt idx]
                 (.setTimestamp stmt idx (Timestamp. (.getTime v)))))

(defn to-pg-json [value]
  (doto (PGobject.)
    (.setType "jsonb")
    (.setValue (generate-string value))))

(extend-protocol jdbc/ISQLValue
  IPersistentMap
  (sql-value [value] (to-pg-json value))
  IPersistentVector
  (sql-value [value] (to-pg-json value)))
