(ns tools.topic-model
  (:require [tools.topic-model.protocol :as proto]
            [tools.topic-model.mallet-lda :as lda]))


(defn summarize
  "Print summary of top n words for all topics."
  [tm n]
  (.displayTopWords tm n false))

(defn topic-count
  "Return number of topics."
  [tm]
  (.getNumTopics tm))

(defn topic-words
  "Return top n words for topic."
  [tm topic n]
  (when (> (topic-count tm) topic)
    (nth (.getTopWords tm n) topic)))

(defn document-probability
  "Return topic probabilities for document index"
  [tm id]
  (.getTopicProbabilities tm id))


(defn train
  ([pipeline n]
   (train pipeline n nil nil))
  ([pipeline args params]
   (train pipeline nil args params))
  ([pipeline n args params]
   (let [source-data  (cond->> (proto/extract pipeline args)
                        n (take n))
         term-vectors (proto/process pipeline args source-data)
         model        (lda/model params term-vectors)]
     {:data  (mapv (fn [i [id data] [id tv]]
                     [id data tv (document-probability model i)])
                   (range (count source-data))
                   source-data
                   term-vectors)
      :model model})))
