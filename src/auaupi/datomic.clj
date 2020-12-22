(ns auaupi.datomic
  (:require
   [datomic.client.api :as d]))

(defn open-connection [{:keys [datomic]}]
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

(defn test-post [client]
  (d/transact (d/connect (d/client client) {:db-name "dogs"})
              {:tx-data [{:dog/id 1
                          :dog/name "Arimendo"
                          :dog/breed "African"
                          :dog/image "https://images.dog.ceo/breeds/african/n02116738_10469.jpg"
                          :dog/port "M"
                          :dog/gender "F"
                          :dog/birth "2020-05-14"
                          :dog/castrated? true
                          :dog/adopted? false}]}))

(defn find-dog-by-id [id]
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
            (d/db (d/connect (d/client {:server-type :dev-local
                                        :storage-dir (str (System/getenv "PWD") "/datomic-data")
                                        :db-name "dogs"
                                        :system "dev"}) {:db-name "dogs"})) id)

       (mapv (fn [[id name breed img port gender birth castrated? adopted?]]
               {:dog/id id
                :dog/name name
                :dog/breed breed
                :dog/img img
                :dog/port port
                :dog/gender gender
                :dog/birth birth
                :dog/castrated? castrated?
                :dog/adopted? adopted?}))))

(find-dog-by-id 1)
(defn get-dogs [conn]
  (let [f false]
    (d/q '[:find ?id ?nome ?breed ?img
           :in $ ?f
           :where
           [?d :dog/id ?id]
           [?d :dog/name ?nome]
           [?d :dog/breed ?breed]
           [?d :dog/image ?img]
           [?d :dog/adopted? ?f]] (d/db conn) f)))

