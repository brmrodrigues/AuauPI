(ns auaupi.service
  (:require
   [io.pedestal.http :as http]
   [auaupi.routes :refer :all]
   [io.pedestal.http.body-params :as body-params]
   [pedestal-api
    [core :as api]]
   [schema.core :as s]))

(def no-csp
  {:name  ::no-csp
   :leave (fn [ctx]
            (assoc-in ctx [:response :headers "Content-Security-Policy"] ""))})

(def doc 
  {:info {:title       "AuauPI"
          :description "The clojure API for dogs adoption"
          :version     "2.0"}
   :tags [{:name         "auaupi"
           :description  ""
           :externalDocs {:description "Find out more"
                          :url         "https://github.com/paygoc6/AuauPI"}}
          {:name        "client"
           :description "Operations about orders"}]})

(def api-routes
  #{["/dogs" :get list-dogs-route] 
    ["/dogs" :post post-dog-route]
    ["/dogs/:id" :get get-dog-route]
    ["/dogs/:id" :post adopt-dog-route]
    ["/swagger.json" :get [(api/negotiate-response)
                           (api/body-params)
                           api/common-body
                           (api/coerce-request)
                           (api/validate-response)
                           api/swagger-json]]
    ["/*resource" :get [(api/negotiate-response)
                        (api/body-params)
                        api/common-body
                        (api/coerce-request)
                        (api/validate-response)
                        no-csp
                        api/swagger-ui]]})

(s/with-fn-validation
  (api/defroutes routes doc api-routes))

(def pedestal-config
  (-> {:env :dev
       ::http/routes #(deref #'routes)
       ::http/router :linear-search
       ::http/resource-path     "/public"
       ::http/type :jetty
       ::http/join? false
       ::http/port 3000
       ::http/allowed-origins   (constantly true)
       ::http/container-options {:h2c? true
                                 :h2?  false
                                 :ssl? false}}
      http/default-interceptors
      (update ::http/interceptors conj (body-params/body-params))))