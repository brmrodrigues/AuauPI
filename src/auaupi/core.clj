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

(defn return-all [_req]
  (map #(into {}
              {:id (:id %)
               :breed (:breed %)
               :name (:name %) 
               :img (:img %)}) @dogs))

(defn get-dogs-handler [_req]
  (-> return-all
      http/json-response))