(ns auaupi.logic-test
  (:require
   [auaupi.logic :as logic]
   [clojure.test :refer [testing deftest is]]
   [matcher-combinators.test :refer [match?]]
   [auaupi.db :as db]))

(deftest logic-functions

  (testing "get url image by breed"
    (is (match? true
                (clojure.string/includes? (logic/get-breed-image! "pitbull") "pitbull"))))

  (testing "summary information dogs"
    (is (match? [{:id "4"
                  :breed "stbernard"
                  :name ""
                  :img (fn [dog] (:img dog) (first @db/dogs))}]
                (logic/response-all @db/dogs))))
  
  (testing "format request"
    (is (match? {:castrated? true}
                (logic/req->treated {:castrated? "true"})))))