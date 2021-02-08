(ns auaupi.swagger
  (:gen-class)
  (:require [io.pedestal.http :as http]
            [io.pedestal.test :as p.test]
            [clojure.java.io :as io]
            [auaupi.service :as service]
            [schema.core :as s]))

(defn -main []
  (let [service (::http/service-fn (http/create-server service/pedestal-config))
        {:keys [status body]} (p.test/response-for service :get "/auaupi/v1/swagger.json")]
    (assert (= 200 status))
    (io/make-parents "doc/swagger/auaupi-v1.json")
    (spit "doc/swagger/auaupi-v1.json" body))
  (shutdown-agents)
  (System/exit 0))