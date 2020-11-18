(ns auaupi.logic
  (:require
   [clj-http.client :as client]
   [clojure.data.json :as json]
   [io.pedestal.http :as http]
   [auaupi.db :as db]
   [clojure.spec.alpha :as s]
   [auaupi.specs :as specs]
   [clojure.edn :as edn]))

(defn filter-dogs [params coll]
<<<<<<< HEAD
  (filter (fn [dog] (and (= params (select-keys dog (keys params)))
                         (= {:adopted? false} (select-keys dog (keys {:adopted? false}))))) coll))
=======
  (filter (fn [dog] (= params (select-keys dog (keys params)))) coll))
>>>>>>> main

(defn response-all [coll]
  (map #(into {}
              {:id (:id %)
               :breed (:breed %)
               :name (:name %)
               :img  (:img  %)}) coll))

(defn req->treated [req]
 (into {}
       (map (fn [[k s]]
              [k (try (let [v (edn/read-string s)]
                        (if (or (number? v)
                                (boolean? v))
                          v
                          s))
                      (catch Throwable ex
                        (println ex)
                        s))]))
       req))


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
  (let [image (get-breed-image! (::specs/breed dog))
        image-added (->> image
                         (assoc dog :img)
                         add-fields)]
    (swap! db/dogs conj image-added)
    (http/json-response image-added)))

(defn valid-dog!
  [dog]
  (cond
    (s/valid? ::specs/dog dog) (create-dog! dog)
    :else {:status 400 :body (json/write-str {:message "Invalid Format"})}))

(defn get-date []
  (quot (System/currentTimeMillis) 1000))

(defn response-adopted [coll]
  (let [dog (->> coll
                 (into {})
                 (map (fn [[k v]] [k v]))
                 (into {}))]
    (if (not (nil? (:name dog)))
      (cond (= (:gender dog) "M")
            {:status 200
             :body (str "Parabéns, você acabou de dar um novo lar para o " (:name dog) "!")}
            (= (:gender dog) "F")
            {:status 200
             :body (str "Parabéns, você acabou de dar um novo lar para a " (:name dog) "!")})
      "Parabéns! Adoção realizada com sucesso")))

(defn dog->adopt [coll]
  (let [dog
        (->> coll
             (into {})
             (map (fn [[k v]] [k v]))
             (into {}))
        pos (.indexOf @db/dogs dog)]
    (swap! db/dogs assoc-in [pos :adopted?] true)
    (swap! db/dogs assoc-in [pos :adoptionDate] (get-date))
    (response-adopted coll)))


(defn get-by-id [req]
  (let [id (:id (:path-params req))]
    (filter #(= id (:id %)) @db/dogs)))

(defn data->response
  [data]
  (cond
    (empty? data) {:status 404 :body (json/write-str "Not Found")}
    :else {:status 200 :body (json/write-str data)}))

<<<<<<< HEAD
(defn get-breeds! [atom]
=======
(defn check-adopted [coll]
  (if (empty? coll)
    {:status 400 :body "Cachorro não está disponível para adoção"}
    (dog->adopt coll)))

(defn get-breeds [atom]
>>>>>>> main
  (let [breeds (-> "https://dog.ceo/api/breeds/list/all"
                   client/get
                   :body
                   (json/read-str :key-fn keyword)
                   :message
                   keys)]
    (swap! atom #(into % breeds))))
<<<<<<< HEAD
=======

>>>>>>> main
