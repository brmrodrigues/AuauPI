(ns auaupi.specs
  (:require 
   [clojure.spec.alpha :as s]))

(s/def ::name string?)
(s/def ::breed string?)
(s/def ::img string?)
(s/def ::age int?)
(s/def ::gender #{"m" "f"})
(s/def ::castrated? boolean?)
(s/def ::port #{"p" "m" "g"})

(s/def ::dog (s/keys :req [::breed ::gender ::port]
                     :opt [::name ::age ::castrated? ::img]))

(defn req->dog [{:keys [json-params]}]
  (let [{:keys [name
                breed
                age
                gender
                castrated?
                port]} json-params]

    {::name name
     ::breed (clojure.string/lower-case breed)
     ::age age
     ::gender (clojure.string/lower-case gender)
     ::castrated? castrated?
     ::port (clojure.string/lower-case port)}))

(comment

  (def d {:auaupi.specs/name "Caramelo", 
          :auaupi.specs/breed "stbernard", 
          :auaupi.specs/age 2, 
          :auaupi.specs/gender "M", 
          :auaupi.specs/castrated? false, 
          :auaupi.specs/port "p"})

  (defn teste
    [dog]
    (s/valid? ::dog dog))

  (teste d))

