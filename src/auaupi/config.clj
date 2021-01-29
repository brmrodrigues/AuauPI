(ns auaupi.config)

(def config-map
  {:dog-ceo {:img ["https://dog.ceo/api/breed/", "/images/random"]
             :breeds "https://dog.ceo/api/breeds/list/all"}
   :datomic {:client-config {:server-type :dev-local
                             :storage-dir (str (System/getenv "PWD") "/datomic-data")
                             :db-name "dogs"
                             :system "dev"}}})