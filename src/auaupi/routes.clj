(ns auaupi.routes
  (:require
   [io.pedestal.http :as http]
   [io.pedestal.interceptor :as io]
   [pedestal-api
    [core :as api]
    [helpers :refer [before defbefore defhandler handler]]]
   [route-swagger.doc :as sw.doc]
   [auaupi.not-logic :as not-logic]
   [auaupi.datomic :as datomic]
   [auaupi.logic :as logic]
   [auaupi.config :as config]
   [schema.core :as s]
   [auaupi.schema :as schema]))

(defn get-dogs [ctx]
  (let [req (get ctx :request)
        result (-> req
                   :params
                   (not-logic/check-params!
                    (datomic/open-connection config/config-map))
                   http/json-response)]
    (assoc ctx :response result)))

(defn get-dog-by-id [ctx]
  (let [req (get ctx :request)
        result (-> req
                   :path-params
                   :id
                   Long/valueOf
                   (datomic/find-dog-by-id
                    (datomic/open-connection config/config-map))
                   logic/datom->dog-full
                   logic/data->response)]
    (assoc ctx :response result)))

(defn post-dogs [ctx]
  (let [req (get ctx :request)]
    (->> ctx
         (not-logic/check-breed! config/config-map)
         (assoc ctx :response))))

(defn post-adoption [ctx]
  (let [req (get ctx :request)
        result (-> req
                   :path-params
                   :id
                   Long/valueOf
                   (not-logic/check-adopted!
                    (datomic/open-connection config/config-map)))]
    (assoc ctx :response result)))

(def list-dogs-route
  (sw.doc/annotate
   {:summary    "List all dogs available for adoption"
    :parameters {:query-params schema/Dog}
    :responses  {200 {:body schema/Dog}
                 400 {:body s/Str}}
    :operationId ::list-dogs}
   (io/interceptor 
    {:name  :response-dogs 
     :enter get-dogs})))

(def get-dog-route
  (sw.doc/annotate
   {:summary    "List all dogs available for adoption"
    :parameters {:path-params {:id s/Int}}
    :responses  {200 {:body schema/Dog}
                 400 {:body s/Str}}
    :operationId ::specific-dog}
   (io/interceptor
    {:name  ::response-specific-dog
     :enter get-dog-by-id})))

(def post-dog-route
  (sw.doc/annotate
   {:summary     "Add a dog to our adoption list"
    :parameters  {:body-params schema/Dog}
    :responses   {201 {:body {:message s/Str}}
                  400 {:body {:message s/Str}}}
    :operationId ::create-dog}
   (io/interceptor
    {:name ::post-dogs
     :enter post-dogs})))

(def adopt-dog-route
  (sw.doc/annotate
   {:summary     "Adopt a dog"
    :parameters  {:path-params {:id s/Int}}
    :responses   {200 {:body s/Str}}
    :operationId ::adopt-dog}
   (io/interceptor
    {:name ::adopt-dog
     :enter post-adoption})))