(ns auaupi.service
  (:require
   [io.pedestal.http :as http]
   [auaupi.routes :refer :all]
   [pedestal-api.core :as api]
   [schema.core :as s]))

(def no-csp
  {:name  ::no-csp
   :leave (fn [ctx]
            (assoc-in ctx [:response :headers "Content-Security-Policy"] ""))})

(s/with-fn-validation
  (api/defroutes
    routes
    {:info {:title       "AuauPI"
            :description "The clojure API for dogs adoption"
            :version     "2.0"}
     :tags [{:name         "auaupi"
             :description  ""
             :externalDocs {:description "Find out more"
                            :url         "https://github.com/paygoc6/AuauPI"}}
            {:name        "client"
             :description "Operations about orders"}]}
    #{["/dogs" :get list-dogs-interceptor]
      ;["/dogs" :get [list-dogs-interceptor]]
      ;["/dogs" :post post-dogs-handler :route-name :post-dogs]
      ;["/dogs/:id" :post post-adoption-handler :route-name :adopt-dogs]
      ;["/dogs/:id" :get get-dog-by-id-handler :route-name :get-by-id]
      ["/swagger.json" :get [(api/negotiate-response) (api/body-params) api/common-body (api/coerce-request) (api/validate-response) api/swagger-json]]
      ["/*resource" :get [(api/negotiate-response) (api/body-params) api/common-body (api/coerce-request) (api/validate-response) no-csp api/swagger-ui]]}))

(def pedestal-config
  (-> {:env                     :prod
       ::http/routes            #(deref #'routes)
       ::http/resource-path     "/public"
       ::http/router            :linear-search
       ::http/allowed-origins   (constantly true)
       ::http/type              :jetty
       ::http/join?             false
       ::http/port              3000
       ::http/container-options {:h2c? true
                                 :h2?  false
                                 :ssl? false}}))