(ns auaupi.not-logic
  (:require
   [clj-http.client :as client]
   [clojure.data.json :as json]
   [io.pedestal.http :as http]
   [auaupi.db :as db]
   [auaupi.logic :as logic]
   [clojure.spec.alpha :as s]
   [auaupi.specs :as specs]))

(defn get-breed-image! [raca]
  (-> (str "https://dog.ceo/api/breed/" (clojure.string/lower-case raca) "/images/random")
      client/get
      :body
      (json/read-str :key-fn keyword)
      :message))

(defn create-dog!
  [{:keys [breed] :as dog}]
  (let [image (get-breed-image! (::specs/breed dog))
        image-added (->> image
                         (assoc dog :img)
                         logic/add-fields)]
    (db/conj-dogs! image-added)
    (http/json-response image-added)))

(defn valid-dog!
  [dog]
  (cond
    (s/valid? ::specs/dog dog) (create-dog! dog)
    :else {:status 400 :body (json/write-str {:message "Invalid Format"})}))

(defn get-breeds! []
  (let [breeds (-> "https://dog.ceo/api/breeds/list/all"
                   client/get
                   :body
                   (json/read-str :key-fn keyword)
                   :message
                   keys)]
    (db/assoc-in-dogs! #(into % breeds))))