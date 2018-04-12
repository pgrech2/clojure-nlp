(ns tools.io
  (:require [clojure.string :as cstr]
            [clojure.java.io :as jio]))

(defn read-file-header
  [delimiter filename]
  (with-open [r (jio/reader  filename)]
    (let [[header & lines] (line-seq r)
          headers          (->> (cstr/split header delimiter)
                                (map (comp keyword #(cstr/replace % #"_" "-"))))]
      (into []
            (comp (map #(cstr/split % #","))
                  (map (partial zipmap headers)))
            lines))))

(defn read-file
  [delimiter filename]
  (with-open [r (jio/reader  filename)]
    (into []
          (map #(cstr/split % delimiter))
          (line-seq r))))
