(ns swagger.service
  (:require
   [io.pedestal.http :as http]
   [routes.routes :as api-routes]
   [io.pedestal.http.route :as route]
   [pedestal-api
    [core :as api]
    [routes :as api.routes]]
   [route-swagger.doc :as sw.doc]
   [schema.core :as s]
   [auaupi.core :as core]))

(def no-csp
  {:name  ::no-csp
   :leave (fn [ctx]
            (assoc-in ctx [:response :headers "Content-Security-Policy"] ""))})

#_(defn build-routes [doc routes]
    (-> routes
        route/expand-routes
        api.routes/replace-splat-parameters
        (api.routes/update-handler-swagger
         (api.routes/comp->> api.routes/default-operation-ids
                             api.routes/default-empty-parameters))
        (sw.doc/with-swagger (merge {:basePath ""} doc))))


(s/with-fn-validation
  (api/defroutes routes
    {:info {:title       "AuauPI"
            :description "The clojure API for dogs adoption"
            :version     "2.0"}
     :tags [{:name         "auaupi"
             :description  ""
             :externalDocs {:description "Find out more"
                            :url         "https://github.com/paygoc6/AuauPI"}}
            {:name        "client"
             :description "Operations about orders"}]}
    core/routes))

(def service {:env                     :prod
              ::http/routes        (init-routes )
              ::http/router        :linear-search
              ::http/type          :jetty
              ::http/port          (Integer. (or (System/getenv "PORT") 8080))})

#_(http/start (http/create-server service))
#_(http/stop (http/create-server service))

(comment 
  '[[["/dogs"
      ["/"
       ^:interceptors [api/error-responses
                       (api/negotiate-response)
                       (api/body-params)
                       api/common-body
                       (api/coerce-request)
                       (api/validate-response)]
       {:get core/get-dogs-handler
        :post core/post-dogs-handler}]

      ["/:id"
       {:get core/get-dog-by-id-handler
        :post core/post-adoption-handler}]]

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