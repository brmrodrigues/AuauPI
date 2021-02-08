(ns auaupi.responses)

(defn response [status body & {:as headers}]
  {:status status :body body :headers headers})

(def ok       (partial response 200))
(def created  (partial response 201))
(def bad-request (partial response 400))
(def not-found (partial response 404))
