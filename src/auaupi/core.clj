(ns auaupi.core
  (:gen-class)
  (:require
   [io.pedestal.http :as http]
   [auaupi.datomic :as datomic]
   [auaupi.config :as config]
   [auaupi.not-logic :as not-logic]
   [swagger.service :as service]))

(defn start []
  (http/start (http/create-server service/pedestal-config)))

(defn create-server []
  (http/create-server service/pedestal-config))

(defn -main [& args]
  (datomic/prepare-datomic! config/config-map)
  (not-logic/get-breeds! config/config-map)
  (start))