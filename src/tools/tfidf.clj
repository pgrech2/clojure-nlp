(ns tools.tfidf)


(defn document-term-frequency
  "Take a tokenizer, document analyzer and document and return a tuple
  of [`document-id` `term` `term-frequency` `document-terms-count`]"
  [tokenizer analyzer document]
  (let [[id text]   document
        term-vector (-> (tokenizer text)
                        (analyzer))
        term-count  (count term-vector)]
    (map (fn [[term freq]]
           [id term freq term-count])
         (frequencies term-vector))))

(defn score-tf
  [[doc-id term freq term-count]]
  [doc-id (double (/ freq term-count))])

(defn term-frequency
  [tokenizer analyzer documents]
  (->> documents
       (mapcat (partial document-term-frequency tokenizer analyzer))
       (map (fn [[doc-id term term-freq doc-term-count :as dtf]]
              [term (score-tf dtf)]))))

(defn score-idf
  [doc-count [term term-freqs]]
  [term (Math/log (/ doc-count (+ 1.0 (count term-freqs))))])

(defn tf-idf
  [tokenizer analyzer documents]
  (let [doc-count   (count documents)
        tf-by-doc   (term-frequency tokenizer analyzer documents)
        idf-by-term (->> (group-by first tf-by-doc)
                         (map (partial score-idf doc-count))
                         (into {}))]
    (into []
          (comp (map #(conj % (get idf-by-term (first %))))
                (map (fn [[term [doc-id tf] idf]]
                       [doc-id term (* tf idf)])))
          tf-by-doc)))
