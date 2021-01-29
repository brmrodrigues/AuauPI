(ns auaupi.swagger
  (:gen-class)
  (:require [io.pedestal.http :as http]
            [io.pedestal.test :as p.test]
            [clojure.java.io :as io]
            [auaupi.service :as service]
            [schema.core :as s]))

(defn response-swagger []
  (let [service (::http/service-fn (http/create-server service/pedestal-config))
        response (-> service
                     (p.test/response-for :get "/auaupi/swagger.json"))]
    (io/make-parents "doc/swagger/swagger.json")
    (prn (:body response))
    (->> response
        :body
        (spit "doc/swagger/swagger.json"))))

(defn -main []
  #_(s/with-fn-validation service/routes)
  (let [service (::http/service-fn (http/create-server service/pedestal-config))
        response (-> service
                     (p.test/response-for :get "/auaupi/swagger.json"))]
    (io/make-parents "doc/swagger/swagger.json")
    (prn (:body response))
    (->> response
         :body
         (spit "doc/swagger/swagger.json")))
  (shutdown-agents)
  (System/exit 0))