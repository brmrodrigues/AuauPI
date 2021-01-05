(ns auaupi.specs
  (:require 
   [clojure.spec.alpha :as s]))

(s/def ::name string?)
(s/def ::breed string?)
(s/def ::img string?)
(s/def ::birth string?)
(s/def ::gender #{"m" "f"})
(s/def ::castrated? boolean?)
(s/def ::port #{"p" "m" "g"})

(s/def ::dog (s/keys :req [::breed ::gender ::port]
                     :opt [::name ::birth ::castrated? ::img]))

(defn req->dog [{:keys [json-params]}]
  (let [{:keys [name
                breed
                birth
                gender
                castrated?
                port]} json-params]

    {::name (if name name "")
     ::breed (clojure.string/lower-case breed)
     ::birth (if birth birth "")
     ::gender (clojure.string/lower-case gender)
     ::castrated? (if castrated? castrated? false)
     ::port (clojure.string/lower-case port)}))