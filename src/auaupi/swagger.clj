(ns auaupi.swagger
  (:require [io.pedestal.http :as http]
            [io.pedestal.test :refer [response-for]]
            [clojure.java.io :as io]
            [auaupi.service :as service]))

(defn -main []
  (let [service (::http/service-fn (http/create-server service/pedestal-config))
        {:keys [status body]} (response-for service :get "/swagger.json")]
    (assert (= 200 status))
    (io/make-parents "doc/swagger/swagger.json")
    (spit "doc/swagger/swagger.json" body))
  #_(shutdown-agents)
  #_(System/exit 0))

#_(response-for 
   (::http/service-fn 
    (http/create-server service/pedestal-config))
   :get "/swagger.json")