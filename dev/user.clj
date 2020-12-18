(ns user
  (:require [auaupi.core :as core]
            [io.pedestal.http :as http]
            [io.pedestal.http.body-params :as body-params]
            [datomic.client.api :as d]
            [auaupi.datomic :as datomic]))


(defonce server (atom nil))

(def dev-pedestal-config
  (-> {::http/routes (fn [] core/routes)
       ::http/type :jetty
       ::http/join? false
       ::http/port 3000}
      http/default-interceptors
      (update ::http/interceptors conj (body-params/body-params))))


(defn start-dev []
  (datomic/prepare-datomic!)
 (when (nil? @server) 
   (reset! server (-> dev-pedestal-config
                      http/create-server
                      http/start))))


(defn stop-dev []
  (when @server
    (http/stop @server)
    (reset! server nil)))

(defn delete-db []
  (let [client (d/client {:server-type :dev-local
                          :storage-dir (str (System/getenv "PWD") "/datomic-data")
                          :db-name "dogs"
                          :system "dev"})]
   (d/delete-database client {:db-name "dogs"} )))

#_(start-dev)
#_(stop-dev)
#_(delete-db)