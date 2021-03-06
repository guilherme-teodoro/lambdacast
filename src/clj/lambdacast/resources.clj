(ns lambdacast.resources
  (:require [liberator.core :refer [defresource]]
            [lambdacast.db :as db]
            [clojure.data.json :as json]
            [monger.json]
            [clojure.tools.logging :as log])
  (:use [feedparser-clj.core]))

(defn auth [req]
  (let [token (get-in req [:request :headers "authorization"])]
    (log/info (str token))
    true))

(defn parse-req [req]
  (-> (get-in req [:request :body])
      slurp
      (json/read-str :key-fn keyword)))

(defn add-podcast [req]
  (-> (:url (parse-req req))
      parse-feed
      db/add-podcast))

(defresource list-podcasts
  :authorized? #(auth %)
  :allowed-methods [:post :get]
  :available-media-types ["application/json"]
  :handle-ok (json/write-str (db/get-podcasts))
  :post! #(add-podcast %))

(defresource auth
  :allowed-methods [:post]
  :available-media-types ["application/json"]
  :post! (fn [] (print " ola ")))

