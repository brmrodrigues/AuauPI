(ns auaupi.logic
  (:require
   [clj-http.client :as client]
   [clojure.data.json :as json]
   [clojure.edn :as edn]))

(defn filter-dogs [params coll]
  (filter (fn [dog] (and (= params (select-keys dog (keys params)))
                         (= {:adopted? false} (select-keys dog (keys {:adopted? false}))))) coll))

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

(defn get-breed-image [raca]
  (-> (str "https://dog.ceo/api/breed/" raca "/images/random")
      client/get
      :body
      (json/read-str :key-fn keyword)
      :message))

