(ns auaupi.logic-test
  (:require
   [auaupi.logic :as logic]
   [clojure.test :refer [testing deftest is]]
   [matcher-combinators.test :refer [match?]]
   [auaupi.db :as db]))

(deftest logic-functions
  (let [dog-test [{:id "0"
                   :name "Bardock"
                   :breed "Mix"
                   :img "https://images.dog.ceo/breeds/mix/piper.jpg"
                   :age 15
                   :gender "M"
                   :castrated? true
                   :port "M"
                   :adopted? true}
                  {:id "1"
                   :name "Leka"
                   :breed "Maltese"
                   :img "https://images.dog.ceo/breeds/maltese/n02085936_4781.jpg"
                   :age 8
                   :gender "F"
                   :castrated? true
                   :port "P"
                   :adopted? false}

                  {:id "2"
                   :name "Xenon"
                   :breed "Weimaraner"
                   :img "https://images.dog.ceo/breeds/weimaraner/n02092339_747.jpg"
                   :age 2
                   :gender "M"
                   :castrated? false
                   :port "G"
                   :adopted? false}]]

    (testing "filter dogs"
      (is (match? '({:breed "Maltese"
                     :castrated? true
                     :age 8
                     :name "Leka"
                     :port "P"
                     :id "1"
                     :gender "F"
                     :adopted? false
                     :img
                     "https://images.dog.ceo/breeds/maltese/n02085936_4781.jpg"})
                  (logic/filter-dogs {:id "1"} dog-test))))

    (testing "change keywords"
      (is (match? {:dog/breed "Mix"
                   :dog/castrated? true
                   :dog/age 15
                   :dog/name "Bardock"
                   :dog/port "M"
                   :dog/id "0"
                   :dog/gender "M"
                   :dog/adopted? true
                   :dog/img "https://images.dog.ceo/breeds/mix/piper.jpg"}
                  (logic/transform-keyword (first dog-test)))))
    
    (testing "summary dogs information"
      (is (match? [#:dog{:id "0"
                         :name "Bardock"
                         :breed "Mix"
                         :img "https://images.dog.ceo/breeds/mix/piper.jpg"}]
                  (logic/response-treated [{:dog/id "0"
                                            :dog/name "Bardock"
                                            :dog/breed "Mix"
                                            :dog/img "https://images.dog.ceo/breeds/mix/piper.jpg"
                                            :dog/age 15
                                            :dog/gender "M"
                                            :dog/castrated? true
                                            :dog/port "M"
                                            :dog/adopted? true}]))))

    (testing "format request"
      (is (match? {:castrated? true}
                  (logic/req->treated {:castrated? "true"}))))))