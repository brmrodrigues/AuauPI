(ns db)

(def dogs
  (atom [{:id "0"
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
          :adopted? true}

         {:id "2"
          :name "Xenon"
          :breed "Weimaraner"
          :img "https://images.dog.ceo/breeds/weimaraner/n02092339_747.jpg"
          :age 2
          :gender "M"
          :castrated? false
          :port "G"
          :adopted? false}

         {:id "3"
          :name "Thor"
          :breed "Pitbull"
          :img "https://images.dog.ceo/breeds/pitbull/IMG_20190826_121528_876.jpg"
          :age 7
          :gender "M"
          :castrated? true
          :port "G"
          :adopted? false}

         {:id "4"
          :name "Thora"
          :breed "Pitbull"
          :img "https://images.dog.ceo/breeds/pitbull/IMG_20190826_121528_876.jpg"
          :age 7
          :gender "F"
          :castrated? true
          :port "G"
          :adopted? false}]))