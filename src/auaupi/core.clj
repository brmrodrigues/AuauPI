(ns auaupi.core
  (:gen-class)
  (:require
   [io.pedestal.http :as http]
   [auaupi.not-logic :as not-logic]
   [auaupi.service :as service]
   [auaupi.config :as config]
   [auaupi.datomic :as datomic]))

(defn start []
  (http/start (http/create-server service/pedestal-config)))

(defn stop []
  (http/stop (http/create-server service/pedestal-config)))

#_ (start)
#_ (stop)

;(defonce server (atom nil))

(defn create-server []
  (http/create-server service/pedestal-config))

(defn -main
  [& args]
  (datomic/prepare-datomic! config/config-map)
  (not-logic/get-breeds! config/config-map)
  (start))