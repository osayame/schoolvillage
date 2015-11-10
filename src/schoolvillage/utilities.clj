(ns schoolvillage.utilities
  (:require [clj-time.core :as time]
            [clj-time.coerce :as coerce]
            [clj-http.client :as client]
            [cheshire.core :refer :all]
            ))

(defn parse-integer [s]
  (if (number? s)
    s
    (try (Integer/parseInt s)
      (catch Exception e nil))))

(defn now-string []
  (coerce/to-string (time/now)))

(defn cut-off [string x]
  (if (> (count string) x)
    (str (.substring string 0 x) "...")
    string))

(defn fmap [f m]
  (into {} (for [[k v] m] [k (f v)])))

(defn fmapkeys [f m]
  (into {} (for [[k v] m] [(f k) v])))

(defn to-boolean [expression]
  (if (.equals expression "nil") (println "") (Boolean/valueOf expression)))

(defn to-int[value]
  (let [value-type (type value)]
    (cond
     (= value-type java.lang.Long) (int value)
     (= value-type java.lang.String) (Integer/parseInt value)
     :else value)))

(defmacro with-timeout [millis & body]
  `(let [future# (future ~@body)]
     (try
       (.get future# ~millis java.util.concurrent.TimeUnit/MILLISECONDS)
       (catch java.util.concurrent.TimeoutException x#
         (do
           (future-cancel future#)
           nil)))))

(defn strip-symbol [string sym]
  (clojure.string/replace string sym ""))

(defn subscribe-mailchimp [fname lname email list-id]
  (client/post "https://us11.api.mailchimp.com/2.0/lists/subscribe.json"
               {:content-type :json
                :throw-exceptions false
                :body (generate-string
                        {:apikey "927d2e12be55ebe1affe340c20517849-us11"
                         :id list-id
                         :double_optin false
                         :email {:email email}
                         :merge_vars {:fname fname :lname lname}
                         })}))
