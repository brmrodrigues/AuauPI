(ns auaupi.logic
  (:require
   [clojure.data.json :as json]
   [auaupi.db :as db]
   [clojure.edn :as edn]
   [auaupi.datomic :as datomic]
   [auaupi.schema :as schema]))

(defn transform-keyword [coll]
  (let [k (keys coll)
        v (vals coll)
        new-k (map (fn [k] (-> k
                               str
                               (clojure.string/replace #":" "dog/")
                               keyword)) k)]
    (->> (map (fn [old new]
                {old new}) k new-k)
         (into {})
         (clojure.set/rename-keys coll))))


(defn filter-dogs [params coll]
  (filter (fn [dog] (= params (select-keys dog (keys params)))) coll))



(defn response-treated [coll]
  (prn coll)
  (map (fn [dog]
         {:dog/id (:dog/id dog)
          :dog/name (:dog/name dog)
          :dog/breed (:dog/breed dog)
          :dog/img (:dog/img dog)}) coll))

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
  [config-map map]
  (hash-map
   :dog/id (datomic/inc-last-id (datomic/open-connection config-map))
   :dog/name (:name map)
   :dog/breed (:breed map)
   :dog/birth (:birth map)
   :dog/gender (:gender map)
   :dog/port (:port map)
   :dog/castrated? (:castrated? map)
   :dog/image (:img map)
   :dog/adopted? false))

(defn get-date []
  (quot (System/currentTimeMillis) 1000))


(defn get-by-id [req]
  (let [id (:id (:path-params req))]
    (filter #(= id (:id %)) @db/dogs)))

(defn data->response
  [data]
  (cond
    (empty? data) {:status 404 :body (json/write-str "Not Found")}
    :else {:status 200 :body (json/write-str data)}))

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

(defn invalid-breed? [breed breeds]
  (empty? (filter #(= (clojure.string/lower-case breed) %) breeds)))

(defn invalid-dog? [dog]
  (not= (schema/validate-schema dog) dog))

(defn invalid-body-response [message]
  (-> {:status 400}
      (assoc :body (json/write-str {:message message}))))
