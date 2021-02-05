(ns auaupi.service
  (:require
   [io.pedestal.http :as http]
   [auaupi.routes :refer :all]
   [io.pedestal.http.body-params :as body-params]
   [pedestal-api
    [core :as api]]
   [schema.core :as s]))

(def common-interceptors
  [(api/negotiate-response)
   (api/body-params)
   api/common-body
   (api/coerce-request)
   (api/validate-response)])

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
  [[["/auaupi/v1"
     ^:interceptors 'common-interceptors

     ["/dogs"
      ^:interceptors []
      {:get  list-dogs-route
       :post post-dog-route}]

      ["/dogs/:id"
       ^:interceptors []
       {:get get-dog-route
        :post adopt-dog-route}]

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
       ::http/allowed-origins   (constantly true)}
      http/default-interceptors
      (update ::http/interceptors conj (body-params/body-params))))