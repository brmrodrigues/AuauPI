(ns auaupi.logic
  (:require
   [clj-http.client :as client]
   [clojure.data.json :as json]))

(defn filter-dogs [params coll]
  (filter (fn [dog] (and (= params (select-keys dog (keys params)))
                         (= {:adopted? false} (select-keys dog (keys {:adopted? false})))))coll))

(defn response-all [coll]
  (map #(into {}
              {:id (:id %) 
               :breed (:breed %)
               :name (:name %)
               :img  (:img  %)}) coll))

(defn trata-req [params]
  (let [param (-> params
                  vals
                  first)
        intParam (-> param
                     int?)
        booleanParam (-> param
                         boolean?)]
    (cond intParam (Integer/valueOf param)
          booleanParam (Boolean/valueOf param)
    :else param)))

(trata-req {:age 7})

(defn get-breed-image [raca]
  (-> (str "https://dog.ceo/api/breed/" raca "/images/random")
      client/get
      :body
      (json/read-str :key-fn keyword)
      :message))

