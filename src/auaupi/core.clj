(ns auaupi.core
  (:gen-class)
  (:require
   [io.pedestal.http :as http]
   [io.pedestal.http.route :as route]
   [io.pedestal.http.body-params :as body-params]
   [auaupi.logic :as logic]
   [auaupi.not-logic :as not-logic]
   [auaupi.datomic :as datomic]
   [pedestal-api
    [core :as api]
    [routes :as api.routes]]
   [schema.core :as s]
   [route-swagger.doc :as sw.doc]
   [io.pedestal.interceptor :as io]))

(def config-map
  {:dog-ceo {:img ["https://dog.ceo/api/breed/", "/images/random"]
             :breeds "https://dog.ceo/api/breeds/list/all"}
   :datomic {:client-config {:server-type :dev-local
                             :storage-dir (str (System/getenv "PWD") "/datomic-data")
                             :db-name "dogs"
                             :system "dev"}}})

(defn get-dogs [ctx]
  #_(let [result (-> config-map
                   datomic/open-connection
                   datomic/find-dogs
                   logic/datom->dog
                   http/json-response)]
    (assoc ctx :response result)))

(defn post-dogs-handler [req]
  (-> req
      (not-logic/check-breed! config-map)
      (not-logic/valid-dog! config-map)
      (datomic/transact-dog! config-map))
  (http/json-response {:status 200 :body "Registered Dog"}))

(defn post-adoption-handler [req]
  (-> req
      :path-params
      :id
      Long/valueOf
      (not-logic/check-adopted!
       (datomic/open-connection config-map))))

(defn get-dog-by-id-handler [req]
  (-> req
      :path-params
      :id
      Long/valueOf
      (datomic/find-dog-by-id
       (datomic/open-connection config-map))
      logic/datom->dog-full
      logic/data->response))

(defn respond-hello [_req]
  {:status 200 :body "Servidor funcionando"})

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

(def list-dogs-interceptor
  (sw.doc/annotate
   {:summary    "List all dogs available for adoption"
    :responses  {200 {:body Dog}
                     400 {:body "Not Found/Empty List"}}
    :operationId ::list-dogs}
   (io/interceptor
    {:name  :get-dogs
     :enter get-dogs})))

(def routes
  #{#_["/" :get respond-hello :route-name :greet]
    #_["/dogs" :get get-dogs-interceptor :route-name :get-dogs]
    ["/dogs" :get [list-dogs-interceptor]]
    #_["/dogs" :post post-dogs-handler :route-name :post-dogs]
    #_["/dogs/:id" :post post-adoption-handler :route-name :adopt-dogs]
    #_["/dogs/:id" :get get-dog-by-id-handler :route-name :get-by-id]
    ["/swagger" :get [api/swagger-json]]})

(def pedestal-config 
  (-> {::http/routes routes
       ::http/type :jetty
       ::http/join? false
       ::http/port 3000}
      http/default-interceptors
      (update ::http/interceptors conj (body-params/body-params))))

(defn start []
  (http/start (http/create-server pedestal-config)))

(defonce server (atom nil))

(defn create-server []
  (http/create-server pedestal-config))

(defn -main [& args]
  (datomic/prepare-datomic! config-map)
  (not-logic/get-breeds! config-map)
  (start))