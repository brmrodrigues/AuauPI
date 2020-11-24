(ns auaupi.core-test
  (:require [auaupi.core :as core]
            [clojure.test :refer [testing deftest is]]
            [matcher-combinators.test :refer [match?]]
            [io.pedestal.http :as http]
            [io.pedestal.test :as http-test]
            [clojure.data.json :as json]
            [auaupi.db :as db]))

(defn make-request! [verb path & args]
  (let [service-fn (::http/service-fn (core/create-server))
        response (apply http-test/response-for service-fn verb path args)]
    (update response :body json/read-str
            :key-fn keyword)))

(deftest dogs-listing-not-adopteds
  (testing "listing dogs"
    (reset! db/dogs [])
    (is (match? {:body [] :status 200}
                (make-request! :get "/dogs"))))
    (testing "testing post route"
      (is (match? {:body {:id "4"
                          :name "caramelo"
                          :breed "stbernard"
                          :age 2
                          :gender "m"
                          :castrated? false
                          :port "p"
                          :adopted? false
                          :img (fn [dog] (:img dog) (first @db/dogs))}}
                  (make-request! :post "/dogs"
                                 :headers {"Content-Type" "application/json"}
                                 :body (json/write-str {:id "4"
                                                        :name "caramelo"
                                                        :breed "stbernard"
                                                        :age 2
                                                        :gender "m"
                                                        :castrated? false
                                                        :port "p"
                                                        :adopted? false})))))

  (testing "listing dog after post"
    (is (match? {:body [{:id "4"
                         :breed "stbernard"
                         :name "caramelo"
                         :img (fn [dog] (:img dog) (first @db/dogs))}] :status 200}
                (make-request! :get "/dogs"))))

  (testing "listing a dog by name"
    (is (match? {:body [{:id "4"
                         :breed "stbernard"
                         :name "caramelo"
                         :img (fn [dog] (:img dog) (first @db/dogs))}] :status 200}
                (make-request! :get "/dogs?name=caramelo"))))

  (testing "listing a dog by breed"
    (is (match? {:body [{:id "4"
                         :breed "stbernard"
                         :name "caramelo"
                         :img (fn [dog] (:img dog) (first @db/dogs))}] :status 200}
                (make-request! :get "/dogs?breed=stbernard"))))

  (testing "testing castrated filter"
    (is (match? {:body [{:id "4"
                         :breed "stbernard"
                         :name "caramelo"
                         :img (fn [dog] (:img dog) (first @db/dogs))}] :status 200}
                (make-request! :get "/dogs?castrated?=false"))))

  (testing "testing age filter"
    (is (match? {:body [{:id "4"
                         :breed "stbernard"
                         :name "caramelo"
                         :img (fn [dog] (:img dog) (first @db/dogs))}] :status 200}
                (make-request! :get "/dogs?age=2"))))

  (testing "testing port filter"
    (is (match? {:body [{:id "4"
                         :breed "stbernard"
                         :name "caramelo"
                         :img (fn [dog] (:img dog) (first @db/dogs))}] :status 200}
                (make-request! :get "/dogs?port=p"))))

  (testing "testing gender filter"
    (is (match? {:body [{:id "4"
                         :breed "stbernard"
                         :name "caramelo"
                         :img (fn [dog] (:img dog) (first @db/dogs))}] :status 200}
                (make-request! :get "/dogs?gender=m"))))

  (testing "listing dog by id"
    (is (match? {:body [{:id "4"
                         :breed "stbernard"
                         :name "caramelo"
                         :img (fn [dog] (:img dog) (first @db/dogs))}] :status 200}
                (make-request! :get "/dogs/4"))))

  #_(testing "adopting a male dog"
    (is (match? {:status 200
                 :body "Parabéns, você acabou de dar um novo lar para o caramelo!"}
                (make-request! :post "/dogs/4"
                               :headers {"Content-Type" "text/plain"})))))
