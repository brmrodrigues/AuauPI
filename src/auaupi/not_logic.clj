(ns auaupi.not-logic
  (:require
   [clj-http.client :as client]
   [clojure.data.json :as json]
   [io.pedestal.http :as http]
   [auaupi.db :as db]
   [auaupi.logic :as logic]
   [clojure.spec.alpha :as s]
   [auaupi.specs :as specs]
   [auaupi.datomic :as datomic]))

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
                 (logic/add-fields config-map))]
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
      :else {:status 400 :body (json/write-str {:message "Invalid Format"})})))

(defn response-adopted! [id conn]
  (let [dog (datomic/get-infos-adopted id conn)]
    (if (not (empty? (ffirst dog)))
      (cond (= (last (first dog)) "m")
            {:status 200
             :body (json/write-str (str "Parabéns, você acabou de dar um novo lar para o " (ffirst dog) "!"))}
            (= (last (first dog)) "f")
            {:status 200
             :body (json/write-str (str "Parabéns, você acabou de dar um novo lar para a " (ffirst dog) "!"))})
      {:status 200 :body (json/write-str "Parabéns! Adoção realizada com sucesso")})))

(defn check-adopted! [id conn]
  (if (ffirst (datomic/get-adoption id conn))
    {:status 400 :body "Cachorro não está disponível para adoção"}
    (do (datomic/adopt-dog id conn)
        (response-adopted! id conn))))

(defn check-params! [params conn]
  (if (empty? params)
    (-> (datomic/find-dogs conn)
        logic/datom->dog)
    (-> params
        logic/transform-keyword
        (logic/filter-dogs (logic/datom->dog-full (datomic/find-all-dogs conn))))))