(ns tools.helpers)

(defn sparse-map
  "Returns a sparse map of features from coll.
   Ex. (sparse-map [5 nil 7]) -> {1 5, 3 7}"
  [coll]
  (into {}
        (comp (map-indexed (fn [i v] [(inc i) v]))
              (keep (fn [[i v]] (when v [i v]))))
        coll))
