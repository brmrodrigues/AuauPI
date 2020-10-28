(ns auaupi.core
  (:require
   [io.pedestal.http :as http]
   [io.pedestal.http.route :as route]
   [clj-http.client :as client]
   [clojure.data.json :as json]
   [io.pedestal.http.body-params :as body-params])
  (:gen-class))

(def dogs
  (atom [{:id "0"
          :name "Bardock"
          :breed "Mix"
          :url "https://images.dog.ceo/breeds/mix/piper.jpg"
          :age 15 :gender "M"
          :castrated? true
          :port "M"
          :adopted? false}

         {:id "1"
          :name "Leka"
          :breed "Pincher"
          :url "https://images.dog.ceo/breeds/maltese/n02085936_4781.jpg"
          :age 8
          :gender "F"
          :castrated? true
          :port "P"
          :adopted? false}

         {:id "2"
          :name "Xenon"
          :breed "Weimaraner"
          :url "https://images.dog.ceo/breeds/weimaraner/n02092339_747.jpg"
          :age 2
          :gender "M"
          :castrated? false
          :port "G"
          :adopted? false}

         {:id "3"
          :name "Thor"
          :breed "Pitbull"
          :url "https://images.dog.ceo/breeds/pitbull/IMG_20190826_121528_876.jpg"
          :age 7
          :gender "M"
          :castrated? true
          :port "G"
          :adopted? false}]))

(defn return-all [coll]
  (map #(into {}
              {:id (:id %)
               :breed (:breed %)
               :name (:name %)
               :img (:img %)}) coll))

(defn get-dogs-handler [_req]
  (-> @dogs
      return-all
      http/json-response))

(defn respond-hello [request]
  {:status 200 :body "Servidor funcionando"})

(def routes
  (route/expand-routes
   #{["/" :get respond-hello :route-name :greet]
     ["/dogs" :get get-dogs-handler :route-name :get-dogs]}))

(def pedestal-config
  (-> {::http/routes routes
       ::http/type :jetty
       ::http/join? false
       ::http/port 3000}
      http/default-interceptors
      (update ::http/interceptors conj (body-params/body-params))))

(defn start []
  (http/start (http/create-server pedestal-config)))

(defonce server (atom nil))

(defn start-dev []
  (reset! server
          (http/start (http/create-server
                       (assoc pedestal-config
                              ::http/join? false)))))


(defn stop-dev []
  (http/stop @server))


(defn restart []
  (stop-dev)
  (start-dev))


#_(restart)
#_(start-dev)
#_(stop-dev)
