(ns auaupi.datomic-test
  (:require
   [clojure.test :refer [testing deftest is]]
   [matcher-combinators.test :refer [match?]]
   [user]
   [auaupi.datomic :as datomic]
   [auaupi.core :as core]
   [helpers]))

(deftest queries
  (user/delete-db)
  (datomic/prepare-datomic! core/config-map)
  (helpers/initial-dogs!)
  (testing "Query consult"
    (is (match?  [[3 "Xenon" "Weimaraner" "https://images.dog.ceo/breeds/weimaraner/n02092339_747.jpg"]
                  [1 "Bardock" "Mix" "https://images.dog.ceo/breeds/mix/piper.jpg"]
                  [5 "Melinda" "Pitbull" "https://images.dog.ceo/breeds/pitbull/IMG_20190826_121528_876.jpg"]
                  [4 "Thor" "Pitbull" "https://images.dog.ceo/breeds/pitbull/IMG_20190826_121528_876.jpg"]]
                 (datomic/get-dogs  (datomic/open-connection core/config-map)))))
  
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
  
  (testing "Update dog"
    (datomic/adopt-dog 1 (datomic/open-connection core/config-map))
    (is (match? [[1
                  "Bardock"
                  "Mix"
                  "https://images.dog.ceo/breeds/mix/piper.jpg"
                  "m"
                  "m"
                  "2017-02-13"
                  true
                  true]]
                (datomic/find-dog-by-id 1 (datomic/open-connection core/config-map))))))


