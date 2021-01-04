(ns auaupi.datomic-test
  (:require
   [clojure.test :refer [testing deftest is]]
   [matcher-combinators.test :refer [match?]]
   [user]
   [auaupi.datomic :as datomic]
   [datomic.client.api :as d]
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
    (is
     (match? [[13194139533321 50 #inst "2021-01-04T14:16:13.592-00:00" 13194139533321 true]
              [87960930222168 74 "Jack" 13194139533321 true]
              [87960930222168 75 "african" 13194139533321 true]
              [87960930222168 78 "m" 13194139533321 true]
              [87960930222168 77 "p" 13194139533321 true]]
             (:tx-data
              (datomic/transact-dog! {:dog/name "Jack"
                                      :dog/breed "african"
                                      :dog/gender "m"
                                      :dog/port "p"}
                                     (datomic/open-connection core/config-map)))))))