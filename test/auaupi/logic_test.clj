(ns auaupi.logic-test
  (:require
   [auaupi.logic :as logic]
   [clojure.test :refer [testing deftest is]]
   [matcher-combinators.test :refer [match?]]
   [auaupi.db :as db]))

(deftest logic-functions
  (reset! db/dogs [{:id "0"
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
                    :adopted? false}])

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
                (logic/filter-dogs {:id "1"} @db/dogs))))

  (testing "summary information dogs"
    (is (match? [{:id "0"
                  :breed "Mix"
                  :name "Bardock"
                  :img "https://images.dog.ceo/breeds/mix/piper.jpg"}
                 {:id "1"
                  :breed "Maltese"
                  :name "Leka"
                  :img "https://images.dog.ceo/breeds/maltese/n02085936_4781.jpg"}
                 {:id "2"
                  :breed "Weimaraner"
                  :name "Xenon"
                  :img
                  "https://images.dog.ceo/breeds/weimaraner/n02092339_747.jpg"}]
                (logic/response-all @db/dogs))))

  (testing "format request"
    (is (match? {:castrated? true}
                (logic/req->treated {:castrated? "true"}))))
  
  (testing "response dog male"
    (is (match? {:body
                 "Parabéns, você acabou de dar um novo lar para o Xenon!"}
                (logic/response-adopted {:id "2"
                                         :name "Xenon"
                                         :breed "Weimaraner"
                                         :img "https://images.dog.ceo/breeds/weimaraner/n02092339_747.jpg"
                                         :age 2
                                         :gender "M"
                                         :castrated? false
                                         :port "G"
                                         :adopted? false}))))
  (testing "response dog female"
    (is (match? {:body
                 "Parabéns, você acabou de dar um novo lar para a Leka!"}
         (logic/response-adopted {:id "1"
                                  :name "Leka"
                                  :breed "Maltese"
                                  :img "https://images.dog.ceo/breeds/maltese/n02085936_4781.jpg"
                                  :age 8
                                  :gender "F"
                                  :castrated? true
                                  :port "P"
                                  :adopted? false})))))