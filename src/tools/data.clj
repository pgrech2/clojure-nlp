(ns tools.data
  (:require [clojure.java.io :as jio]
            [clojure.string :as cstr]
            [taoensso.timbre :as log]
            [com.stuartsierra.component :as component]

            [tools.data.proto :as proto]))

(defrecord Loader [config source]

  component/Lifecycle
  (start [this]
    (log/info "Starting Extractor")
    (let [{:keys [file
                  parse]} config]
      (if source
        (do (log/info "Data already loaded...")
            this)
        (do (log/info "Loading data from" file)
            (assoc this :source (parse file))))))

  (stop [this]
    (log/info "Stopping Extractor")
    (if source
      (assoc this :source nil)
      (do (log/info "No data loaded...")
          this)))

  proto/Loader
  (records [this]
    source)
  (records [this transform]
    (into []
          (map transform)
          source))
  (records [this transform tokenize]
    (into []
          (comp (map transform)
                (map tokenize))
          source))
  (records [this transform tokenize analyze]
    (into []
          (comp (map transform)
                (map tokenize)
                (map analyze))
          source)))


(defn loader
  [config]
  (map->Loader {:config config}))

(defn records
  "Transform must return tuple:
   `[id (optional other features) text]`
   Transform -> Analyze must return tuple:
   `[id (optional other features) text]`
   Transform -> Analyze -> Tokenize must return tuple:
   `[id (optional other features) [term vector]]`

   Where id is an incremental and unique primary key and `optional
  other features` are other features related to the document."
  ([data]
   (proto/records data))
  ([data transform]
   (proto/records data transform))
  ([data transform tokenize]
   (proto/records data transform tokenize))
  ([data transform tokenize analyze]
   (proto/records data transform tokenize analyze)))
