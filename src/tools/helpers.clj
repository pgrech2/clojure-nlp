(ns tools.helpers)

(defn sparse-map
  "Returns a sparse map of features from seq.  The map will be suitable
  as the second entry in a dataset vector.  seq should have length
  equal to the total number of features in the dataset and set empty
  values to nil.
  Ex. (sparse-map [5 nil 7]) -> {1 5, 3 7}"
  [seq]
  (->> seq
       (map vector (rest (range)))
       (keep (fn [[i v]] (when v [i v])))
       (into (sorted-map))))
