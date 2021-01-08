(ns swagger.swagger
  (:require [pedestal-api.core :as pedestal-api]
            [io.pedestal.http :as bootstrap]
            [io.pedestal.interceptor.chain :refer [terminate]]
            [io.pedestal.interceptor :refer [interceptor]]
            [io.pedestal.http.route :as route]
            [pedestal-api
             [core :as api]
             [helpers :refer [before defbefore defhandler handler]]
             [routes :as api.routes]]
            [route-swagger.doc :as sw.doc]
            [ring.swagger.swagger2 :as rs]
            [schema.core :as s]
            [auaupi.core :as core]))

(s/defschema Dog 
  {:name s/Str
   :bred s/Str
   :gender s/Str (s/enum "m" "f")
   :port s/Str (s/enum "p" "m" "g")})

(def no-csp
  {:name ::no-csp
   :leave (fn [ctx]
            (assoc-in ctx [:response :headers "Content-Security-Policy"] ""))})

(s/with-fn-validation
 (rs/swagger-json
  {:info {:title       "Swagger Sample App built using pedestal-api"
          :description "Find out more at https://github.com/oliyh/pedestal-api"
          :version     "2.0"}
   :tags [{:name         "pets"
           :description  "Everything about your Pets"
           :externalDocs {:description "Find out more"
                          :url         "http://swagger.io"}}
          {:name        "orders"
           :description "Operations about orders"}]}
  [[["/" ^:interceptors [api/error-responses
                         (api/negotiate-response)
                         (api/body-params)
                         api/common-body
                         (api/coerce-request)
                         (api/validate-response)]
     ["/dogs" ^:interceptors [(api/doc {:tags ["pets"]})]
      ["/" {:get core/get-dogs-handler
            :post core/post-dogs-handler}]
      ["/:id" {:get core/get-dog-by-id-handler
               :put core/post-adoption-handler}]]
 
     ["/swagger.json" {:get api/swagger-json}]
     ["/*resource" ^:interceptors [no-csp] {:get api/swagger-ui}]]]]))

(rs/swagger-json {:swagger "2.0"
                  :info {:title "Swagger API", :version "0.0.1"}
                  :produces ["application/json"]
                  :consumes ["application/json"]
                  :paths {}
                  :definitions {}})
