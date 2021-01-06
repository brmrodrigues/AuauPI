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

(deftest testing-routes
  (testing "listing dogs"
    (is (match? {:body [{:dog/id 3
                         :dog/name "Xenon"
                         :dog/breed "Weimaraner"
                         :dog/img
                         "https://images.dog.ceo/breeds/weimaraner/n02092339_747.jpg"}
                        {:dog/id 1
                         :dog/name "Bardock"
                         :dog/breed "Mix"
                         :dog/img
                         "https://images.dog.ceo/breeds/mix/piper.jpg"}
                        {:dog/id 5
                         :dog/name "Melinda"
                         :dog/breed "Pitbull"
                         :dog/img
                         "https://images.dog.ceo/breeds/pitbull/IMG_20190826_121528_876.jpg"}
                        {:dog/id 4
                         :dog/name "Thor"
                         :dog/breed "Pitbull"
                         :dog/img
                         "https://images.dog.ceo/breeds/pitbull/IMG_20190826_121528_876.jpg"}] :status 200}
                (make-request! :get "/dogs"))))
  (testing "listing dog by id"
    (is (match? {:body [{:img "https://images.dog.ceo/breeds/weimaraner/n02092339_747.jpg"
                         :breed "Weimaraner"
                         :castrated? false
                         :port "g"
                         :name "Xenon"
                         :id 3
                         :birth "2018-08-14"
                         :gender "m"
                         :adopted? false}] :status 200}
                (make-request! :get "/dogs/3"))))
  (testing "testing post route"
    (is (match? {:body {:status 200
                        :body "Registered Dog"}}
                (make-request! :post "/dogs"
                               :headers {"Content-Type" "application/json"}
                               :body (json/write-str {:name "Caramelo"
                                                      :breed "stbernard"
                                                      :age 2
                                                      :gender "m"
                                                      :castrated? false
                                                      :port "p"
                                                      :adopted? false})))))
  (testing "listing dog after post"
    (is (match? {:body [{:breed "stbernard"
                         :castrated? false
                         :name "Caramelo"
                         :port "p"
                         :id 6
                         :gender "m"
                         :adopted? false
                         :img (fn [dog] (:img dog) (first @db/dogs))
                         :birth ""}] :status 200}
                (make-request! :get "/dogs/6"))))
  (testing "testing adopt a dog"
    (is (match? {:body "Parabéns, você acabou de dar um novo lar para o Caramelo!"}
                (make-request! :post "/dogs/6"
                               :headers {"Content-Type" "application/json"}))))
  #_(testing "listing a dog by name" ;;CRIAR FIND NO DATOMIC PELO NAME
      (is (match? {:body [{:id "4"
                           :breed "stbernard"
                           :name "caramelo"
                           :img (fn [dog] (:img dog) (first @db/dogs))}] :status 200}
                  (make-request! :get "/dogs?name=Caramelo"))))

  #_(testing "listing a dog by breed" ;;CRIAR FIND NO DATOMIC PELA BREED
      (is (match? {:body [{:id "4"
                           :breed "stbernard"
                           :name "caramelo"
                           :img (fn [dog] (:img dog) (first @db/dogs))}] :status 200}
                  (make-request! :get "/dogs?breed=stbernard"))))

  #_(testing "testing castrated filter" ;;CRIAR FIND NO DATOMIC PELO CASTRATED
      (is (match? {:body [{:id "4"
                           :breed "stbernard"
                           :name "caramelo"
                           :img (fn [dog] (:img dog) (first @db/dogs))}] :status 200}
                  (make-request! :get "/dogs?castrated?=false"))))

  #_(testing "testing age filter"
      (is (match? {:body [{:id "4"
                           :breed "stbernard"
                           :name "caramelo"
                           :img (fn [dog] (:img dog) (first @db/dogs))}] :status 200}
                  (make-request! :get "/dogs?age=2"))))

  #_(testing "testing port filter"
      (is (match? {:body [{:id "4"
                           :breed "stbernard"
                           :name "caramelo"
                           :img (fn [dog] (:img dog) (first @db/dogs))}] :status 200}
                  (make-request! :get "/dogs?port=p"))))

  #_(testing "testing gender filter"
      (is (match? {:body [{:id "4"
                           :breed "stbernard"
                           :name "caramelo"
                           :img (fn [dog] (:img dog) (first @db/dogs))}] :status 200}
                  (make-request! :get "/dogs?gender=m"))))
)
