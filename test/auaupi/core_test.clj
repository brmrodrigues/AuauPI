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
                      :adopted? false}
                     
                     {:id "4"
                      :name "Thora"
                      :breed "Pitbull"
                      :img "https://images.dog.ceo/breeds/pitbull/IMG_20190826_121528_876.jpg"
                      :age 7
                      :gender "F"
                      :castrated? true
                      :port "G"
                      :adopted? false}
                     {:id "5"
                      :name ""
                      :breed "Pitbull"
                      :img "https://images.dog.ceo/breeds/pitbull/IMG_20190826_121528_876.jpg"
                      :age 7
                      :gender "M"
                      :castrated? false
                      :port "M"
                      :adopted? false}])
    
    (is (match? {:body [{:id "2"
                         :name "Xenon"
                         :breed "Weimaraner"
                         :img "https://images.dog.ceo/breeds/weimaraner/n02092339_747.jpg"}
                        {:id "4"
                         :breed "Pitbull"
                         :name "Thora"
                         :img "https://images.dog.ceo/breeds/pitbull/IMG_20190826_121528_876.jpg"}
                        {:id "5"
                         :breed "Pitbull"
                         :name ""
                         :img "https://images.dog.ceo/breeds/pitbull/IMG_20190826_121528_876.jpg"}]
                 :status 200}
                (make-request! :get "/dogs"))))
  
  (testing "listing dog by id" 
    (is (match? {:body [{:id "2"
                        :name "Xenon"
                        :breed "Weimaraner"
                        :img "https://images.dog.ceo/breeds/weimaraner/n02092339_747.jpg"
                        :age 2
                        :gender "M"
                        :castrated? false
                        :port "G"
                        :adopted? false}] :status 200}
                (make-request! :get "/dogs/2"))))
  
  (testing "listing dog not found"
    (is (match? {:body "Not Found" :status 404}
                (make-request! :get "/dogs/40"))))

  (testing "testing age filter"
    (is (match? {:body [{:id "2"
                         :breed "Weimaraner"
                         :name "Xenon"
                         :img "https://images.dog.ceo/breeds/weimaraner/n02092339_747.jpg"}] :status 200}
                (make-request! :get "/dogs?age=2"))))

  (testing "testing gender filter"
    (is (match? {:body [{:id "4"
                         :breed "Pitbull"
                         :name "Thora"
                         :img "https://images.dog.ceo/breeds/pitbull/IMG_20190826_121528_876.jpg"}] :status 200}
                (make-request! :get "/dogs?gender=F"))))

  (testing "testing castrated filter"
    (is (match? {:body [{:id "4"
                         :breed "Pitbull"
                         :name "Thora"
                         :img "https://images.dog.ceo/breeds/pitbull/IMG_20190826_121528_876.jpg"}] :status 200}
                (make-request! :get "/dogs?castrated?=true"))))

  (testing "testing breed filter"
    (is (match? {:body [{:id "2"
                         :name "Xenon"
                         :breed "Weimaraner"
                         :img "https://images.dog.ceo/breeds/weimaraner/n02092339_747.jpg"}] :status 200}
                (make-request! :get "/dogs?breed=Weimaraner"))))

  (testing "adopting a dog without name"
    (is (match? {:body "Parabéns! Adoção realizada com sucesso"}
                (make-request! :post "/dogs/5"
                               :headers {"Content-Type" "application/json"}))))
  
  (testing "adopting a female dog"
    (is (match? {:body "Parabéns, você acabou de dar um novo lar para a Thora!"}
                (make-request! :post "/dogs/4"
                               :headers {"Content-Type" "application/json"}))))
  
  (testing "adopting a male dog"
    (is (match? {:body "Parabéns, você acabou de dar um novo lar para o Xenon!"}
                (make-request! :post "/dogs/2"
                               :headers {"Content-Type" "application/json"}))))
  
(testing "testing port filter"
    (is (match? {:body [{:id "2"
                         :name "Xenon"
                         :breed "Weimaraner"
                         :img "https://images.dog.ceo/breeds/weimaraner/n02092339_747.jpg"}
                        {:id "4"
                         :breed "Pitbull"
                         :name "Thora"
                         :img "https://images.dog.ceo/breeds/pitbull/IMG_20190826_121528_876.jpg"}] :status 200}
                (make-request! :get "/dogs?port=G"))))


  (reset! db/dogs [])
  (let [dog-test {:name "Caramelo"
                  :breed "stbernard"
                  :age 2
                  :gender "M"
                  :castrated? false
                  :port "p"}]
    (testing "testing post route"
      (is (match? {:body {:breed "stbernard"
                          :castrated? false
                          :age 2
                          :name "Caramelo"
                          :port "p"
                          :id "4"
                          :gender "m"
                          :adopted? false
                          :img (fn [dog] (:img dog) (first @db/dogs))}}
                  (make-request! :post "/dogs"
                                 :headers {"Content-Type" "application/json"}
                                 :body (json/write-str dog-test)))))
    (testing "listing dog after post"
      (is (match? {:body [{:id "4"
                           :breed "stbernard"
                           :name "Caramelo"
                           :img (fn [dog] (:img dog) (first @db/dogs))}] :status 200}
                  (make-request! :get "/dogs")))))
  (testing "inserting invalid format"
    (is (match? {:body {:message "Invalid Format"}}
                (make-request! :post "/dogs"
                               :headers {"Content-Type" "application/json"}
                               :body (json/write-str {:name true
                                                      :breed 3.5
                                                      :age "2"
                                                      :gender 2
                                                      :castrated? "false"
                                                      :port "j"})))))
  (reset! db/dogs [])
  (testing "inserting without the optional parameters"
    (is (match? {:body {:breed "stbernard"
                        :castrated? false
                        :age 0
                        :name ""
                        :port "p"
                        :id "4"
                        :gender "m"
                        :adopted? false
                        :img (fn [dog] (:img dog) (first @db/dogs))}
                 :status 200}
                (make-request! :post "/dogs"
                               :headers {"Content-Type" "application/json"}
                               :body (json/write-str {:breed "stbernard"
                                                      :gender "M"
                                                      :port "p"}))))))
