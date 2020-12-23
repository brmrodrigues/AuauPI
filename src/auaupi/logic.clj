(ns auaupi.logic
  (:require
   [clojure.data.json :as json]
   [auaupi.db :as db]
   [clojure.edn :as edn]
   [auaupi.datomic :as datomic]
   [auaupi.core :as core]))

(defn filter-dogs [params coll]
  (filter (fn [dog] (= params (select-keys dog (keys params)))) coll))

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

(defn add-fields
  [map]
  (hash-map
   :dog/id (datomic/inc-last-id (datomic/open-connection core/config-map))
   :dog/name (:auaupi.specs/name map)
   :dog/breed (:auaupi.specs/breed map)
   :dog/birth (:auaupi.specs/birth map)
   :dog/gender (:auaupi.specs/gender map)
   :dog/port (:auaupi.specs/port map)
   :dog/castrated? (:auaupi.specs/castrated? map)
   :dog/image (:img map)
   :dog/adopted? false))

(defn get-date []
  (quot (System/currentTimeMillis) 1000))

(defn response-adopted [coll]
  (let [dog (->> coll
                 (into {})
                 (map (fn [[k v]] [k v]))
                 (into {}))]
    (if (not (empty? (:name dog)))
      (cond (= (:gender dog) "m")
            {:status 200
             :body (json/write-str (str "Parabéns, você acabou de dar um novo lar para o " (:name dog) "!"))}
            (= (:gender dog) "f")
            {:status 200
             :body (json/write-str (str "Parabéns, você acabou de dar um novo lar para a " (:name dog) "!"))})
      {:status 200 :body (json/write-str "Parabéns! Adoção realizada com sucesso")})))

(defn dog->adopt [coll]
  (let [dog
        (->> coll
             (into {})
             (map (fn [[k v]] [k v]))
             (into {}))
        pos (.indexOf @db/dogs dog)]
    (db/assoc-in-dogs! [pos :adopted?] true)
    (db/assoc-in-dogs! [pos :adoptionDate] (get-date))
    (response-adopted coll)))

(defn get-by-id [req]
  (let [id (:id (:path-params req))]
    (filter #(= id (:id %)) @db/dogs)))

(defn data->response
  [data]
  (cond
    (empty? data) {:status 404 :body (json/write-str "Not Found")}
    :else {:status 200 :body (json/write-str data)}))

(defn check-adopted [coll]
  (if (empty? coll)
    {:status 400 :body "Cachorro não está disponível para adoção"}
    (dog->adopt coll)))

(defn datom->dog [coll]
  (->> coll (mapv (fn [[id name breed img]]
                    {:dog/id id
                     :dog/name name
                     :dog/breed breed
                     :dog/img img}))))

(defn datom->dog-full [coll]
  (->> coll (mapv (fn [[id name breed img port gender birth castrated? adopted?]]
                    {:dog/id id
                     :dog/name name
                     :dog/breed breed
                     :dog/img img
                     :dog/port port
                     :dog/gender gender
                     :dog/birth birth
                     :dog/castrated? castrated?
                     :dog/adopted? adopted?}))))