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
   [schema.core :as s]))

(s/defschema Dog
  {:id s/Int
   :name s/Str
   :breed s/Str
   :image s/Str
   :birth s/Str
   :gender (s/enum "m" "f")
   :port (s/enum "p" "m" "g")
   :castrated? s/Bool
   :adopted? s/Bool})

(defn respond-hello [_req]
  {:status 200 :body "Servidor funcionando"})

(defn get-dogs [_req]
  (-> config/config-map
      datomic/open-connection
      datomic/find-dogs
      logic/datom->dog
      http/json-response))

(defn post-dogs [ctx]
  (let [req (get ctx :request)]
    (-> req
        (not-logic/check-breed! config/config-map)
        (not-logic/valid-dog! config/config-map)
        (datomic/transact-dog! config/config-map)))
  (http/json-response {:status 200 :body "Registered Dog"}))

(defn post-adoption [req]
  (-> req
      :path-params
      :id
      Long/valueOf
      (not-logic/check-adopted!
       (datomic/open-connection config/config-map))))

(defn get-dog-by-id [req]
  (-> req
      :path-params
      :id
      Long/valueOf
      (datomic/find-dog-by-id
       (datomic/open-connection config/config-map))
      logic/datom->dog-full
      logic/data->response))


(def list-dogs-route
  (sw.doc/annotate
   {:summary    "List all dogs available for adoption"
    :parameters {:query-params (s/enum :breed :name :port :gender :castrated?)}
    :responses  {200 {:body Dog}
                 400 {:body "Not Found/Empty List"}}
    :operationId ::list-dogs}
   (io/interceptor
    {:name  :response-dogs 
     :enter get-dogs})))

(def get-dog-route
  (sw.doc/annotate
   {:summary    "List all dogs available for adoption"
    :parameters {:path-params {:id s/Int}}
    :responses  {200 {:body Dog}
                 400 {:body "Not Found/Empty List"}}
    :operationId ::specific-dog}
   (io/interceptor
    {:name  ::response-specific-dog
     :enter get-dog-by-id})))

(def post-dog-route
  (handler
   ::create-pet
   {:summary     "Add a dog to our adoption list"
    :parameters  {:body-params Dog}
    :responses   {201 {:body Dog}}
    :operationId ::create-dog}
   post-dogs))

(def adopt-dog-route
  (before
   ::update-pet
   {:summary     "Update a pet"
    :parameters  {:path-params {:id s/Int}
                  :body-params Dog}
    :responses   {200 {:body s/Str}}
    :operationId ::adopt-dog}
   post-adoption))