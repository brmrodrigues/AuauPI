(ns auaupi.core-test
  (:require
   [clojure.test :refer [testing deftest is]]
   [matcher-combinators.test :refer [match?]]
   [user]
   [auaupi.datomic :as datomic]
   [auaupi.core :as core]
   [helpers]))

(defn make-request! [verb path & args]
  (let [service-fn (::http/service-fn (core/create-server))
        response (apply http-test/response-for service-fn verb path args)]
    (update response :body json/read-str
            :key-fn keyword)))

(deftest dogs-listing-not-adopteds
  (user/delete-db)
  (datomic/prepare-datomic! core/config-map)
  (helpers/initial-dogs!)
  (testing "Query consult"
    (is (match?  [[3 "Xenon" "Weimaraner" "https://images.dog.ceo/breeds/weimaraner/n02092339_747.jpg"]
                  [1 "Bardock" "Mix" "https://images.dog.ceo/breeds/mix/piper.jpg"]
                  [5 "Melinda" "Pitbull" "https://images.dog.ceo/breeds/pitbull/IMG_20190826_121528_876.jpg"]
                  [4 "Thor" "Pitbull" "https://images.dog.ceo/breeds/pitbull/IMG_20190826_121528_876.jpg"]]
                 (datomic/find-dogs  (datomic/open-connection core/config-map)))))


  (testing "New dog"
    (is (match?  [6
                  "Elo"
                  "African"
                  "f"
                  "p"
                  "2016-07-21"]

                 (let [coll
                       (:tx-data
                        (datomic/transact-dog!
                         {:dog/id (datomic/inc-last-id (datomic/open-connection core/config-map))
                          :dog/name "Elo"
                          :dog/breed "African"
                          :dog/gender "f"
                          :dog/port "p"
                          :dog/birth "2016-07-21"} core/config-map))]
                   (into [] (rest (map (fn [coll] (nth coll 2 :not-found)) coll)))))))
  
  (testing "One dog"
    (is (match?  [[2
                   "Leka"
                   "Maltese"
                   "https://images.dog.ceo/breeds/maltese/n02085936_4781.jpg"
                   "p"
                   "f"
                   "2019-05-05"
                   true
                   true]]
                 (datomic/find-dog-by-id 2 (datomic/open-connection core/config-map)))))
  #_(testing "listing dog after post"
      (is (match? {:body [{:id "4"
                           :breed "stbernard"
                           :name "caramelo"
                           :img (fn [dog] (:img dog) (first @db/dogs))}] :status 200}
                  (make-request! :get "/dogs"))))

  #_(testing "listing a dog by name"
      (is (match? {:body [{:id "4"
                           :breed "stbernard"
                           :name "caramelo"
                           :img (fn [dog] (:img dog) (first @db/dogs))}] :status 200}
                  (make-request! :get "/dogs?name=caramelo"))))

  #_(testing "listing a dog by breed"
      (is (match? {:body [{:id "4"
                           :breed "stbernard"
                           :name "caramelo"
                           :img (fn [dog] (:img dog) (first @db/dogs))}] :status 200}
                  (make-request! :get "/dogs?breed=stbernard"))))

  #_(testing "testing castrated filter"
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

  #_(testing "listing dog by id"
      (is (match? {:body [{:id "4"
                           :breed "stbernard"
                           :name "caramelo"
                           :img (fn [dog] (:img dog) (first @db/dogs))}] :status 200}
                  (make-request! :get "/dogs/4"))))

  #_(testing "testing adopt a dog"
      (is (match? {:body "Parabéns, você acabou de dar um novo lar para o caramelo!"}
                  (make-request! :post "/dogs/4"
                                 :headers {"Content-Type" "application/json"})))))
