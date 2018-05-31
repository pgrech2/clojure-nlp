(ns clojure-nlp.topic-model
  (:require [clojure-nlp.topic-model.mallet-lda :as lda]))


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
  ([records]
   (train records nil))
  ([records params]
   (let [model (lda/model params records)]
     {:data  (map (fn [index [id term-vector]]
                    [id term-vector (document-probability model index)])
                  (range (count records))
                  records)
      :model model})))
