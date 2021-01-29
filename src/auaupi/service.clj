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

;(def api-routes
;  #{["/dogs" :get list-dogs-route]
;    ["/dogs" :post (conj common-interceptors 'post-dog-route)]
;    ["/dogs/:id" :get get-dog-route]
;    ["/dogs/:id" :post adopt-dog-route]
;    ["/auaupi/swagger.json" :get (conj common-interceptors 'api/swagger-json)]
;    ["/*resource" :get [no-csp api/swagger-ui]]})

(def api-routes
  [[["/auaupi/v1"
     ^:interceptors [(api/negotiate-response)
                     (api/body-params)
                     api/common-body
                     (api/coerce-request)
                     (api/validate-response)]

     ["/dogs"
      ^:interceptors []
      {:get list-dogs-route
       :post post-dog-route}]

     ["/swagger.json"
      ^:interceptors []
      {:get api/swagger-json}]

     ["/*resource"
      ^:interceptors [no-csp]
      {:get api/swagger-ui}]]]])

(s/with-fn-validation
  (api/defroutes routes doc api-routes))

(def pedestal-config
  (-> {::http/routes routes
       ::http/router :linear-search
       ::http/type :jetty
       ::http/join? false
       ::http/port 3000
       ::http/host "0.0.0.0"
       ::http/allowed-origins (constantly true)}))

;;;;TESTE DA ROTA POST /dogs
;curl --request post localhost:3000/dogs -H 'Content-Type: application/json' -d '{"name": "dog-nome", "breed": "breed-type", "birth": "123456", "gender": "m", "port": "m", "castrated?": true}'