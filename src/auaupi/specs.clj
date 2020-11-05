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

(s/def ::dog (s/keys :req [::breed ::img ::gender ::port]
                     :opt [::name ::age ::castrated?]))

(defn req->dog [{:keys [json-params]}]
  (let [{:keys [name
                breed
                img
                age
                gender
                castrated?
                port]} json-params]

    {::name name
     ::breed breed
     ::img img
     ::age age
     ::gender gender
     ::castrated? castrated?
     ::port port}))