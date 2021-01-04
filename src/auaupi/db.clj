(ns auaupi.db)

(def dogs
  (atom [{:id "0"
          :name "Bardock"
          :breed "Mix"
          :img "https://images.dog.ceo/breeds/mix/piper.jpg"
          :age 15
          :gender "m"
          :castrated? true
          :port "m"
          :adopted? true}

         {:id "1"
          :name "Leka"
          :breed "Maltese"
          :img "https://images.dog.ceo/breeds/maltese/n02085936_4781.jpg"
          :age 8
          :gender "f"
          :castrated? true
          :port "p"
          :adopted? true}

         {:id "2"
          :name "Xenon"
          :breed "Weimaraner"
          :img "https://images.dog.ceo/breeds/weimaraner/n02092339_747.jpg"
          :age 2
          :gender "m"
          :castrated? false
          :port "g"
          :adopted? false}

         {:id "3"
          :name "Thor"
          :breed "Pitbull"
          :img "https://images.dog.ceo/breeds/pitbull/IMG_20190826_121528_876.jpg"
          :age 7
          :gender "m"
          :castrated? false
          :port "g"
          :adopted? false}

         {:id "4"
          :name ""
          :breed "Pitbull"
          :img "https://images.dog.ceo/breeds/pitbull/IMG_20190826_121528_876.jpg"
          :age 7
          :gender "f"
          :castrated? true
          :port "g"
          :adopted? false}]))

(def breeds (atom []))

(defn assoc-in-dogs! [path v]
  (swap! dogs assoc-in path v))

(defn conj-dogs! [v]
  (swap! dogs conj v))

(defn conj-breeds! [v]
  (swap! breeds #(into % v)))