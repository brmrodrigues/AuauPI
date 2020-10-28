(ns auaupi.core
  (:require
   [io.pedestal.http :as http]
   [io.pedestal.http.route :as route]
   [clj-http.client :as client]
   [clojure.data.json :as json]
   [io.pedestal.http.body-params :as body-params])
  (:gen-class))

(defn return-all [request]
  (map #(into {}
              {:id (:id %)
               :breed (:breed %)
               :name (:name %)
               :img (:img %)}
              ) @dogs))

(defn get-dogs-handler [request]
  (-> return-all
      http/json-response))