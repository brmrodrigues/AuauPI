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
                 (datomic/find-dogs  (datomic/open-connection core/config-map)))))
  
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
                   (into [] (rest (map (fn [coll] (nth coll 2 :not-found)) coll))))))))