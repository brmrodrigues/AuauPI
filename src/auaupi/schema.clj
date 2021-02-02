(ns auaupi.schema
  (:require
   [schema.core :as s]))

(s/defschema Dog 
             {(s/optional-key :name) s/Str
              :breed s/Str
              (s/optional-key :birth) s/Str
              :gender (s/enum "m" "f")
              :port (s/enum "p" "m" "g")
              (s/optional-key :castrated?) s/Bool})

(defn validate-schema [dog]
  (try (s/validate Dog dog)
       (catch Exception _e false)))