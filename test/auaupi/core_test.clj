(ns auaupi.core-test
  (:require [auaupi.core :as core]
            [clojure.test :refer [testing deftest is]]
            [matcher-combinators.test :refer [match?]]
            [io.pedestal.http :as http]
            [io.pedestal.test :as http-test]
            [clojure.data.json :as json]
            [db :as db]))

(defn make-request! [verb path & args]
  (let [service-fn (::http/service-fn (core/create-server))
        response (apply http-test/response-for service-fn verb path args)]
    (update response :body json/read-str
            :key-fn keyword)))

(deftest dogs-listing-not-adopteds
  (testing "listing dogs"
    (reset! db/dogs [{:id "1"
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
                        :adopted? false}])
    (is (match? {:body [{:id "2"
                         :name "Xenon"
                         :breed "Weimaraner"
                         :img "https://images.dog.ceo/breeds/weimaraner/n02092339_747.jpg"}]
                 :status 200} 
                (make-request! :get "/dogs")))))