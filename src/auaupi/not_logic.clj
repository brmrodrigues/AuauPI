(ns auaupi.not-logic
  (:require
   [clj-http.client :as client]
   [clojure.data.json :as json]
   [io.pedestal.http :as http]
   [auaupi.logic :as logic]
   [auaupi.datomic :as datomic]
   [auaupi.schema :as schema]
   [auaupi.responses :refer :all]))

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
  [coll config-map]
  (let [image (get-breed-image! (:breed coll) config-map)
        dog (->> image
                 (assoc coll :img)
                 (logic/add-fields config-map))]
    (datomic/transact-dog! dog config-map)
    (created "Registered dog")))

(defn valid-dog!
  [dog config-map]
  (if (= (schema/validate-schema dog) dog)
    (create-dog! dog config-map)
    (bad-request "Invalid dog")))

(defn get-breeds! [{:keys [dog-ceo]}]
  (let [breeds (-> dog-ceo
                   :breeds
                   client/get
                   :body
                   (json/read-str :key-fn keyword)
                   :message
                   keys)]
    (map #(name %) breeds)))

(defn check-breed! [config-map req]
  (let [breed (:breed (:json-params req))
        dog (:json-params req)
        breeds (get-breeds! config-map)]
    (cond
      (not
       (empty? (filter #(= (clojure.string/lower-case breed) %) breeds)))
       (valid-dog! dog config-map)
      :else (bad-request "Invalid breed"))))

(defn response-adopted! [id conn]
  (let [dog (datomic/get-infos-adopted id conn)]
    (if (not (empty? (ffirst dog)))
      (cond (= (last (first dog)) "m")
            (ok (str "Parabéns, você acabou de dar um novo lar para o " (ffirst dog) "!"))
            (= (last (first dog)) "f")
            (ok (str "Parabéns, você acabou de dar um novo lar para a " (ffirst dog) "!")))
      (ok "Parabéns! Adoção realizada com sucesso!"))))

(defn check-adopted! [id conn]
  (if (ffirst (datomic/get-adoption id conn))
    (bad-request "Cachorro não está disponível para adoção!")#_{:status 400 :body "Cachorro não está disponível para adoção"}
    (do (datomic/adopt-dog id conn)
        (response-adopted! id conn))))

(defn check-params! [params conn]
  (if (empty? params)
    (-> (datomic/find-dogs conn)
        logic/datom->dog)
    (-> params
        logic/transform-keyword
        (logic/filter-dogs (logic/datom->dog-full (datomic/find-all-dogs conn)))
        logic/response-treated)))