(ns auaupi.not-logic
  (:require
   [clj-http.client :as client]
   [clojure.data.json :as json]
   [io.pedestal.http :as http]
   [auaupi.db :as db]
   [auaupi.logic :as logic]
   [clojure.spec.alpha :as s]
   [auaupi.specs :as specs]))

(defn get-breed-image! [raca {:keys [dog-ceo]}]
  (-> (str (-> dog-ceo
               :img
               first)
           (clojure.string/lower-case raca)
           (-> dog-ceo
               :img
               second))
      client/get
      :body
      (json/read-str :key-fn keyword)
      :message))

(defn create-dog!
  [{:keys [breed] :as dog} config-map]
  (let [image (get-breed-image! (::specs/breed dog) config-map)
        dog (->> image
                         (assoc dog :img)
                         logic/add-fields)]
    #_(db/conj-dogs! dog)
    dog))

(defn valid-dog!
  [dog config-map]
  (cond
    (s/valid? ::specs/dog dog) (create-dog! dog config-map)
    :else {:status 400 :body (json/write-str {:message "Invalid Format"})}))

(defn get-breeds! [{:keys [dog-ceo]}]
  (let [breeds (-> dog-ceo
                   :breeds
                   client/get
                   :body
                   (json/read-str :key-fn keyword)
                   :message
                   keys)]
    (db/conj-breeds! breeds)))

(defn check-breed! [req config-map]
  (get-breeds! config-map)
  (let [breed (:breed (:json-params req))]
    (cond
      (not (empty?
            (filter #(= (keyword breed) %) @db/breeds))) (specs/req->dog req)
      :else {:status 400 :body (json/write-str {:message "Invalid Breed"})})))