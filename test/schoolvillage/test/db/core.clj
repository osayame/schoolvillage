(ns schoolvillage.test.db.core
  (:require [schoolvillage.db.core :as db]
            [schoolvillage.db.migrations :as migrations]
            [clojure.test :refer :all]
            [clojure.java.jdbc :as jdbc]
            [conman.core :refer [with-transaction]]
            [environ.core :refer [env]]))

(use-fixtures
  :once
  (fn [f]
    (db/connect!)
    (f)))

(deftest test-users
  (with-transaction [t-conn db/*conn*]
    (jdbc/db-set-rollback-only! t-conn)
    (is (= "FL" (:state (db/add-user
               {:id         "0"
                :first_name "Sam"
                :last_name  "Smith"
                :email      "sam.smith@example.com"
                :phone      "4049810115"
                :city       "Miami"
                :state      "FL"
                :address1   "9871 W. Fern Lane"
                :photo      ""
                :zip        "33025"
                :biography  "Smashing Lad"
                :resume     ""
                }))))
    ))
