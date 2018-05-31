(ns clojure-nlp.text)


(defn document-term-frequency
  "Term frequency by term for a document.
   Return tuple of:
   [`document-id` `term` `term-frequency` `document-term-count`]"
  [document]
  (let [[id term-vector] document
        term-count       (count term-vector)]
    (map (fn [[term freq]]
           [id term freq term-count])
         (frequencies term-vector))))

(defn- score-tf
  "Calculate term frequence by document."
  [freq term-count]
  (double (/ freq term-count)))

(defn term-frequency
  "Return term frequency normalized by number of terms in document."
  [documents]
  (->> documents
       (mapcat document-term-frequency)
       (map (fn [[doc-id term term-freq doc-term-count :as dtf]]
              [term [doc-id (score-tf term-freq doc-term-count)]]))))

(defn term-document-frequency
  "Return term frequency normalized by occurence in documents within
  corpus."
  [documents]
  (let [doc-count (count documents)]
    (->> documents
         (mapcat (fn [[id terms]]
                   (map (partial vector id) terms)))
         (group-by last)
         (map (fn [[term docs]]
                [term (score-tf (count (set (map first docs))) doc-count)]))
         (into {}))))

(defn- score-idf
  "Calculate inverse document frequency by term."
  [doc-count [term term-freqs]]
  [term (Math/log (/ doc-count (+ 1.0 (count term-freqs))))])

(defn tfidf
  "Return term frequency inverse document frequency by unique document
  and term."
  [documents]
  (let [doc-count   (count documents)
        tf-by-doc   (term-frequency documents)
        idf-by-term (->> (group-by first tf-by-doc)
                         (map (partial score-idf doc-count))
                         (into {}))]
    (into []
          (comp (map #(conj % (get idf-by-term (first %))))
                (map (fn [[term [doc-id tf] idf]]
                       [doc-id term (* tf idf)])))
          tf-by-doc)))
