(ns helpers
  (:require
   [auaupi.core :as core]
   [auaupi.datomic :as datomic]
   [datomic.client.api :as d]))

(defn initial-dogs! []
  (let [first-dogs [{:dog/id 1
                     :dog/name "Bardock"
                     :dog/breed "Mix"
                     :dog/image "https://images.dog.ceo/breeds/mix/piper.jpg"
                     :dog/birth "2017-02-13"
                     :dog/gender "m"
                     :dog/castrated? true
                     :dog/port "m"
                     :dog/adopted? false}
                    
                    {:dog/id 2
                     :dog/name "Leka"
                     :dog/breed "Maltese"
                     :dog/image "https://images.dog.ceo/breeds/maltese/n02085936_4781.jpg"
                     :dog/birth "2019-05-05"
                     :dog/gender "f"
                     :dog/castrated? true
                     :dog/port "p"
                     :dog/adopted? true}

                    {:dog/id 3
                     :dog/name "Xenon"
                     :dog/breed "Weimaraner"
                     :dog/image "https://images.dog.ceo/breeds/weimaraner/n02092339_747.jpg"
                     :dog/birth "2018-08-14"
                     :dog/gender "m"
                     :dog/castrated? false
                     :dog/port "g"
                     :dog/adopted? false}

                    {:dog/id 4
                     :dog/name "Thor"
                     :dog/breed "Pitbull"
                     :dog/image "https://images.dog.ceo/breeds/pitbull/IMG_20190826_121528_876.jpg"
                     :dog/birth "2019-07-29"
                     :dog/gender "m"
                     :dog/castrated? false
                     :dog/port "g"
                     :dog/adopted? false}

                    {:dog/id 5
                     :dog/name "Melinda"
                     :dog/breed "Pitbull"
                     :dog/image "https://images.dog.ceo/breeds/pitbull/IMG_20190826_121528_876.jpg"
                     :dog/birth "2016-01-27"
                     :dog/gender "f"
                     :dog/castrated? true
                     :dog/port "g"
                     :dog/adopted? false}]]
    (d/transact (datomic/open-connection core/config-map) {:tx-data first-dogs})))