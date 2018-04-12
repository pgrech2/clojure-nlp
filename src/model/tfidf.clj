(ns model.tfidf)


(defn document-term-frequency
  "Take a tokenizer, document analyzer and document and return a tuple
  of [`document-id` `term` `term-frequency` `document-terms-count`]"
  [document]
  (let [[id term-vector] document
        term-count       (count term-vector)]
    (map (fn [[term freq]]
           [id term freq term-count])
         (frequencies term-vector))))

(defn score-tf
  "Calculate term frequence by document."
  [[doc-id term freq term-count]]
  [doc-id (double (/ freq term-count))])

(defn train-term-frequency
  "Return term frequency by document for corpus of term vectors."
  [documents]
  (->> documents
       (mapcat document-term-frequency)
       (map (fn [[doc-id term term-freq doc-term-count :as dtf]]
              [term (score-tf dtf)]))))

(defn score-idf
  "Calculate inverse document frequency by term."
  [doc-count [term term-freqs]]
  [term (Math/log (/ doc-count (+ 1.0 (count term-freqs))))])

(defn train
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
