(ns routes.routes
  (:require
   [io.pedestal.interceptor :as io]
   [pedestal-api
    [core :as api]
    [helpers :refer [before defbefore defhandler handler]]]
   [route-swagger.doc :as sw.doc]
   [auaupi.core :as core]
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

(defn list-dogs-route []
  (sw.doc/annotate
   {:summary    "List all dogs available for adoption"
    :parameters {:query-params (s/enum :breed :name :port :gender :castrated?)}
    :responses  {200 {:body Dog}
                 400 {:body "Not Found/Empty List"}}
    :operationId ::list-dogs}
   (io/interceptor
    {:name  :response-dogs
     :enter core/get-dogs-handler})))

(defn get-dog-route []
  (sw.doc/annotate
   {:summary    "List all dogs available for adoption"
    :parameters {:path-params {:id s/Int}}
    :responses  {200 {:body Dog}
                 400 {:body "Not Found/Empty List"}}
    :operationId ::specific-dog}
   (io/interceptor
    {:name  ::response-specific-dog
     :enter core/get-dog-by-id-handler})))

(def post-dog-route
  (handler
   ::create-pet
   {:summary     "Add a dog to our adoption list"
    :parameters  {:body-params Dog}
    :responses   {201 {:body Dog}}
    :operationId ::create-dog}
   core/post-dogs-handler))

(def adopt-dog-route
  "Example of using the before helper"
  (before
   ::update-pet
   {:summary     "Update a pet"
    :parameters  {:path-params {:id s/Int}
                  :body-params Dog}
    :responses   {200 {:body s/Str}}
    :operationId ::adopt-dog}
   core/post-adoption-handler))