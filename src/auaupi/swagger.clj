(ns auaupi.swagger
  (:require [io.pedestal.http :as http]
            [io.pedestal.test :refer [response-for]]
            [clojure.java.io :as io]
            [auaupi.service :as service]))

(defn -main []
  (let [service (::http/service-fn (http/create-server service/pedestal-config))
        {:keys [status body]} (response-for service :get "/auaupi/swagger.json")]
    (assert (= 200 status))
    (io/make-parents "doc/swagger/swagger.json")
    (spit "doc/swagger/swagger.json" body))
  (shutdown-agents)
  (System/exit 0))