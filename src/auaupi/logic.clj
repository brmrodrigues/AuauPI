(ns auaupi.logic
  (:require
   [clj-http.client :as client]
   [clojure.data.json :as json]))

(defn filter-dogs [params dogs]
  (filter (fn [dog] (if params
                      (and (= (first (vals params)) (String/valueOf (first (vals (select-keys dog (keys params))))))
                           (= {:adopted? false} (select-keys dog (keys {:adopted? false}))))
                      (= {:adopted? false} (select-keys dog (keys {:adopted? false})))))
          dogs))

(defn return-all [args coll]
  (map #(into {}
              {:id (:id %)
               :breed (:breed %)
               :name (:name %)
               :img  (:img  %)}) (filter-dogs args coll)))

(defn get-breed-image [raca]
  (-> (str "https://dog.ceo/api/breed/" raca "/images/random")
      client/get
      :body
      (json/read-str :key-fn keyword)
      :message))