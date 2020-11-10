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
                      :adopted? false}])
    (is (match? {:body [{:id "2"
                         :name "Xenon"
                         :breed "Weimaraner"
                         :img "https://images.dog.ceo/breeds/weimaraner/n02092339_747.jpg"}
                        {:id "4"
                         :breed "Pitbull"
                         :name "Thora"
                         :img "https://images.dog.ceo/breeds/pitbull/IMG_20190826_121528_876.jpg"}]
                 :status 200}
                (make-request! :get "/dogs"))))

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

  (testing "testing post route"
    (let [dog-test {:id "7"
                    :name "Robert"
                    :breed "French Bulldog"
                    :img "https://images.dog.ceo/breeds/bulldog-french/n02108915_57.jpg"
                    :age 15
                    :gender "M"
                    :castrated? true
                    :port "M"
                    :adopted? true}]
      (is (match? {:body [dog-test] :status 201}
                  (make-request! :post "/dogs"
                                 :headers {"Content-Type" "application/json"}
                                 :body (json/encode dog-test))))
  
      (testing "listing dog after post"
        (is (match? {:body [dog-test] :status 200}
                    (make-request! :get "/dogs")))))))
