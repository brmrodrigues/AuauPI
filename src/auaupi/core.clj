(ns auaupi.core
  (:gen-class)
  (:require
   [io.pedestal.http :as http]
   [io.pedestal.http.route :as route]
   [io.pedestal.http.body-params :as body-params]
   [auaupi.db :as db]
   [auaupi.logic :as logic]
   [auaupi.specs :as specs]))

(defn get-dogs-handler [req]
  (-> req
      (:params {})
      logic/req->treated
      (logic/filter-dogs @db/dogs)
      logic/response-all
      http/json-response))

(defn post-dogs-handler [req]
  (prn (specs/req->dog req))
  (-> req
      specs/req->dog
      logic/valid-dog?))

(defn get-dog-by-id-handler [req]
  (let [id (:id (:path-params req))]
    (-> id
        logic/get-by-id
        http/json-response)))

(defn respond-hello [_req]
  {:status 200 :body "Servidor funcionando"})

(def routes
  (route/expand-routes
   #{["/" :get respond-hello :route-name :greet]
     ["/dogs" :get get-dogs-handler :route-name :get-dogs]
     ["/dogs" :post post-dogs-handler :route-name :post-dogs]
     ["/dogs/:id" :get get-dog-by-id-handler :route-name :get-by-id]}))

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
  (start))