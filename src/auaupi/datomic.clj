(ns auaupi.datomic
  (:require
   [datomic.client.api :as d]))

(defn open-connection! [{:keys [datomic]}]
  (let [client (d/client (-> datomic
                             :client-config))]
    (d/create-database client {:db-name (-> datomic
                                            :client-config
                                            :db-name)})
    (d/connect client {:db-name (-> datomic
                                    :client-config
                                    :db-name)})))

(defn create-schema [client]
  (let [schema [{:db/ident :dog/id
                 :db/valueType :db.type/long
                 :db/cardinality :db.cardinality/one
                 :db/doc "Dog's id"}

                {:db/ident :dog/name
                 :db/valueType :db.type/string
                 :db/cardinality :db.cardinality/one
                 :db/doc "Dog's name"}

                {:db/ident :dog/breed
                 :db/valueType :db.type/string
                 :db/cardinality :db.cardinality/one
                 :db/doc "Dog's breed"}

                {:db/ident :dog/image
                 :db/valueType :db.type/string
                 :db/cardinality :db.cardinality/one
                 :db/doc "Dog's image"}

                {:db/ident :dog/port
                 :db/valueType :db.type/string
                 :db/cardinality :db.cardinality/one
                 :db/doc "Dog's port"}

                {:db/ident :dog/gender
                 :db/valueType :db.type/string
                 :db/cardinality :db.cardinality/one
                 :db/doc "Dogs's gender"}

                {:db/ident :dog/birth
                 :db/valueType :db.type/string
                 :db/cardinality :db.cardinality/one
                 :db/doc "Dog's birth"}

                {:db/ident :dog/castrated?
                 :db/valueType :db.type/boolean
                 :db/cardinality :db.cardinality/one
                 :db/doc "Dog's castrated?"}

                {:db/ident :dog/adopted?
                 :db/valueType :db.type/boolean
                 :db/cardinality :db.cardinality/one
                 :db/doc "Dog's is adopted?"}]]
    (d/transact (d/connect client {:db-name "dogs"})
                {:tx-data schema})))


(defn prepare-datomic! [{:keys [datomic]}]
  (let [client (d/client (-> datomic
                             :client-config))]
    (d/create-database client {:db-name (-> datomic
                                            :client-config
                                            :db-name)})
    (create-schema client)))

(defn find-dog-by-id [id conn]
  (->> (d/q '[:find ?id ?n ?b ?i ?p ?g ?birth ?c ?a
              :in $ ?id
              :where
              [?e :dog/id ?id]
              [?e :dog/name ?n]
              [?e :dog/breed ?b]
              [?e :dog/image ?i]
              [?e :dog/port ?p]
              [?e :dog/gender ?g]
              [?e :dog/birth ?birth]
              [?e :dog/castrated? ?c]
              [?e :dog/adopted? ?a]]
            (d/db conn) id)))

(defn get-dogs! [conn]
  (let [f false]
    (d/q '[:find ?id ?nome ?breed ?img
           :in $ ?f
           :where
           [?d :dog/id ?id]
           [?d :dog/name ?nome]
           [?d :dog/breed ?breed]
           [?d :dog/image ?img]
           [?d :dog/adopted? ?f]] (d/db conn) f)))

