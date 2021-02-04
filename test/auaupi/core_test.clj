(ns auaupi.core-test
  (:require [auaupi.core :as core]
            [auaupi.config :as config]
            [auaupi.datomic :as datomic]
            [auaupi.service :as service]
            [clojure.test :refer [testing deftest is]]
            [matcher-combinators.test :refer [match?]]
            [io.pedestal.http :as http]
            [io.pedestal.test :as http-test]
            [clojure.data.json :as json]
            [helpers]
            [user]))

(defn make-request! [verb path & args]
  (let [service-fn (::http/service-fn (core/create-server))
        response (apply http-test/response-for service-fn verb path args)]
    (update response :body json/read-str
            :key-fn keyword)))

(deftest testing-routes
  (user/delete-db)
  (datomic/prepare-datomic! config/config-map)
  (helpers/initial-dogs!)
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
                (make-request! :get "/auaupi/v1/dogs"))))
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
                (make-request! :get "/auaupi/v1/dogs/3"))))
  (testing "testing post route"
    (is (match? {:status 201
                 :body   {:message "Registered dog"}}
                (make-request!
                  :post "/auaupi/v1/dogs"
                  :headers {"Content-Type" "application/json"}
                  :body (json/write-str {:name "Thor"
                                         :breed "Pitbull"
                                         :birth "2019-07-29"
                                         :gender "m"
                                         :castrated? false
                                         :port "g"})))))
  (testing "listing dog after post"
    (let [response (make-request! :get "/auaupi/v1/dogs/6")]
      (is (match? {:body [{:breed "Pitbull",
                         :castrated? false,
                         :name "Thor",
                         :port "g",
                         :id 6,
                         :gender "m",
                         :adopted? false,
                         :birth "2019-07-29"}]
                 :status 200}
                {:body [(-> response
                            (get-in [:body 0])
                            (dissoc :img))]
                 :status (:status response)}))))
  (testing "testing adopt a dog"
    (is (match? {:body "Parabéns, você acabou de dar um novo lar para o Thor!"}
                (make-request! :post "/auaupi/v1/dogs/6"
                               :headers {"Content-Type" "application/json"}))))

  (testing "listing a dog by name" ;;CRIAR FIND NO DATOMIC PELO NAME
      (is (match? {:body [{:dog/id 1,
                           :dog/name "Bardock",
                           :dog/breed "Mix",
                          :dog/img "https://images.dog.ceo/breeds/mix/piper.jpg"}] :status 200}
                  (make-request! :get "/auaupi/v1/dogs?name=Bardock"))))

  (testing "listing a dog by breed" ;;CRIAR FIND NO DATOMIC PELA BREED
      (is (match? {:body [{:dog/id 5
                           :dog/name "Melinda"
                           :dog/breed "Pitbull"
                           :dog/img
                           "https://images.dog.ceo/breeds/pitbull/IMG_20190826_121528_876.jpg"}
                           {:dog/id 4
                            :dog/name "Thor"
                            :dog/breed "Pitbull"
                            :dog/img
                            "https://images.dog.ceo/breeds/pitbull/IMG_20190826_121528_876.jpg"}
                          ] :status 200}
                  (make-request! :get "/auaupi/v1/dogs?breed=Pitbull"))))

  (testing "testing castrated filter" ;;CRIAR FIND NO DATOMIC PELO CASTRATED
      (is (match? {:body [] :status 200}
                  (make-request! :get "/auaupi/v1/dogs?castrated?=false"))))

  (testing "testing port filter"
      (is (match? {:body [{:dog/id 1
                           :dog/name "Bardock"
                           :dog/breed "Mix"
                           :dog/img "https://images.dog.ceo/breeds/mix/piper.jpg"}] :status 200}
                  (make-request! :get "/auaupi/v1/dogs?port=m"))))

  (testing "testing gender filter"
      (is (match? {:body [#:dog{:id 2
                                :name "Leka"
                                :breed "Maltese"
                                :img
                                "https://images.dog.ceo/breeds/maltese/n02085936_4781.jpg"}
                          #:dog{:id 5
                                :name "Melinda"
                                :breed "Pitbull"
                                :img
                                "https://images.dog.ceo/breeds/pitbull/IMG_20190826_121528_876.jpg"}] :status 200}
                  (make-request! :get "/auaupi/v1/dogs?gender=f")))))
