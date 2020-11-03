(ns auaupi.core
  (:require
   [io.pedestal.http :as http]
   [io.pedestal.http.route :as route]
   [clj-http.client :as client]
   [clojure.data.json :as json]
   [io.pedestal.http.body-params :as body-params]
   [clojure.spec.alpha :as s])
  (:gen-class))

(def dogs
  (atom [{:id "0"
          :name "Bardock"
          :breed "Mix"
          :img "https://images.dog.ceo/breeds/mix/piper.jpg"
          :age 15 
          :gender "M"
          :castrated? true
          :port "M"
          :adopted? true}

         {:id "1"
          :name "Leka"
          :breed "Pincher"
          :img "https://images.dog.ceo/breeds/maltese/n02085936_4781.jpg"
          :age 8
          :gender "F"
          :castrated? true
          :port "P"
          :adopted? true}

         {:id "2"
          :name "Xenon"
          :breed "Weimaraner"
          :img "https://images.dog.ceo/breeds/weimaraner/n02092339_747.jpg"
          :age 2
          :gender "M"
          :castrated? false
          :port "G"
          :adopted? false}

         {:id "3"
          :name "Thor"
          :breed "Pitbull"
          :img "https://images.dog.ceo/breeds/pitbull/IMG_20190826_121528_876.jpg"
          :age 7
          :gender "M"
          :castrated? true
          :port "G"
          :adopted? false}]))

(s/def ::name string?)
(s/def ::breed string?)
(s/def ::img string?)
(s/def ::age int?)
(s/def ::gender string?)
(s/def ::castrated? boolean?)
(s/def ::port string?)

(s/def ::dog (s/keys :req [::name ::breed ::img ::age ::gender ::castrated? ::port]))

(defn req->dog [{:keys [json-params]}]
  (let [{:keys [name
                breed
                img
                age
                gender
                castrated?
                port]}json-params]
    
    {::name name
     ::breed breed
     ::img img
     ::age age
     ::gender gender
     ::castrated? castrated?
     ::port port}))
         

(defn filter-dogs [params dogs]
  (filter (fn [dog] (= params (select-keys dog (keys params))))
          dogs))

(defn return-all [coll]
  (map #(into {}
              {:id (:id %)
               :breed (:breed %)
               :name (:name %)
               :img  (:img  %)}) coll))

(defn get-dogs-handler [_req]
  (-> {:adopted? false}
      (filter-dogs @dogs)
      return-all
      http/json-response))

(defn respond-hello [_req]
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

(defn create-server []
  (http/create-server pedestal-config))