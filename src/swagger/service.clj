(ns swagger.service
  (:require
   [io.pedestal.http :as http]
   [routes.routes :as api-routes]
   [io.pedestal.http.route :as route]
   [pedestal-api
    [core :as api]
    [routes :as api.routes]]
   [route-swagger.doc :as sw.doc]
   [schema.core :as s]))

(def no-csp
 {:name  ::no-csp
  :leave (fn [ctx]
          (assoc-in ctx [:response :headers "Content-Security-Policy"] ""))})

(defn build-routes [doc routes]
  (-> routes
      route/expand-routes
      api.routes/replace-splat-parameters
      (api.routes/update-handler-swagger
       (api.routes/comp->> api.routes/default-operation-ids
                           api.routes/default-empty-parameters))
      (sw.doc/with-swagger (merge {:basePath ""} doc))))

(defn init-routes [routes]
  (s/with-fn-validation
    (build-routes
     {:info {:title       "AuauPI"
             :description "The clojure API for dogs adoption"
             :version     "2.0"}
      :tags [{:name         "auaupi"
              :description  ""
              :externalDocs {:description "Find out more"
                             :url         "https://github.com/paygoc6/AuauPI"}}
             {:name        "client"
              :description "Operations about orders"}]}
     routes)))


(def service {:env                     :prod
              ::http/routes        (init-routes '[[["/dogs"
                                                    ["/"
                                                     ^:interceptors [api/error-responses
                                                                     (api/negotiate-response)
                                                                     (api/body-params)
                                                                     api/common-body
                                                                     (api/coerce-request)
                                                                     (api/validate-response)]
                                                     {:get api-routes/list-dogs-route
                                                      :post api-routes/post-dog-route}]

                                                    ["/:id"
                                                     {:get api-routes/get-dog-route
                                                      :put api-routes/adopt-dog-route}]]

                                                   ["/swagger.json" ^:interceptors [(api/negotiate-response)
                                                                                    (api/body-params)
                                                                                    api/common-body
                                                                                    (api/coerce-request)
                                                                                    (api/validate-response)]
                                                    {:get api/swagger-json}]

                                                   ["/*resource"
                                                    ^:interceptors [(api/negotiate-response)
                                                                    (api/body-params)
                                                                    api/common-body
                                                                    (api/coerce-request)
                                                                    (api/validate-response)
                                                                    no-csp]
                                                    {:get api/swagger-ui}]]])
              ::http/router        :linear-search
              ::http/type          :jetty
              ::http/port          (Integer. (or (System/getenv "PORT") 8080))})

#_(http/start (http/create-server service))
#_(http/stop (http/create-server service))