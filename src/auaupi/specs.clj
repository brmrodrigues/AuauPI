(ns auaupi.specs
  (:require 
   [clojure.spec.alpha :as s]))

(s/def ::name string?)
(s/def ::breed string?)
(s/def ::img string?)
(s/def ::age int?)
(s/def ::gender string?)
(s/def ::castrated? boolean?)
(s/def ::port string?)

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
     ::breed breed
     ::age age
     ::gender gender
     ::castrated? castrated?
     ::port port}))

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

