(ns auaupi.logic
  (:require
   [clj-http.client :as client]
   [clojure.data.json :as json]
   [io.pedestal.http :as http]
   [auaupi.db :as db]
   [clojure.spec.alpha :as s]
   [auaupi.specs :as specs]))

(defn filter-dogs [params dogs]
  (filter (fn [dog] (if params
                      (and (= (first (vals params)) (String/valueOf (first (vals (select-keys dog (keys params))))))
                           (= {:adopted? false} (select-keys dog (keys {:adopted? false}))))
                      (= {:adopted? false} (select-keys dog (keys {:adopted? false})))))
          dogs))

(defn return-all [args coll]
  (map #(into {}
              {:id (:id %)
               :breed (:breed %)
               :name (:name %)
               :img  (:img  %)}) (filter-dogs args coll)))

(defn get-breed-image! [raca]
  (-> (str "https://dog.ceo/api/breed/" (clojure.string/lower-case raca) "/images/random")
      client/get
      :body
      (json/read-str :key-fn keyword)
      :message))

(defn add-fields
  [map]
  (hash-map
   :id "4"
   :name (:auaupi.specs/name map)
   :breed (:auaupi.specs/breed map)
   :age (:auaupi.specs/age map)
   :gender (:auaupi.specs/gender map)
   :port (:auaupi.specs/port map)
   :castrated? (:auaupi.specs/castrated? map)
   :img (:img map)
   :adopted? false))

(defn create-dog!
  [{:keys [breed] :as dog}]
  (let [image (get-breed-image! (:auaupi.specs/breed dog))]
    (prn image)
    (->> image
         (assoc dog :img)
         add-fields
         (swap! db/dogs conj))
    (-> dog
        (assoc :img image)
        add-fields
        http/json-response)))

(s/fdef create-dog!
  :args (s/cat :dog ::dog))
