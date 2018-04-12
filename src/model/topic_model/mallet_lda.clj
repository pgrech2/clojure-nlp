(ns model.topic-model.mallet-lda
  (:import [cc.mallet.types
            Alphabet
            FeatureSequence
            Instance
            InstanceList]
           [cc.mallet.topics
            ParallelTopicModel]))

;; Thanks to:
;; https://github.com/marcliberatore/mallet-lda

(defn- make-feature-sequence
  "Instantiate a features sequence with a colleciton of tokens using
  the provided Alphabet to handle symbols."
  [tokens alphabet]
  (let [feature-sequence (FeatureSequence. alphabet)]
    (doseq [token tokens]
      (.add feature-sequence token))
    feature-sequence))

(defn- make-instance
  "Instantiate an Instance with an id and token collection,
  using the provided Alphabet to handle symbols."
  [id tokens alphabet]
  (let [feature-sequence (make-feature-sequence tokens alphabet)]
    (Instance. feature-sequence nil id nil)))

(defn make-instance-list
  "Make an InstanceList using a collection of documents. A document is
  a pairing of a document id and the collection of tokens for that
  document. Document ids must be unique, and tokens must be strings."
  [documents]
  (let [alphabet      (Alphabet.)
        instance-list (InstanceList. alphabet nil)]
    (doseq [[id tokens] documents]
      (.add instance-list (make-instance id tokens alphabet)))
    instance-list))

(defn model
  "Return a topic model (ParallelTopicModel) on the given
  instance-list, using the optional parameters if specified. The
  default parameters will run fairly quickly, but will not return
  high-quality topics."
  [parameters documents]
  (let [{:keys [num-topics
                num-iter
                optimize-interval
                optimize-burn-in
                num-threads
                random-seed]
         :or   {num-topics        10
                num-iter          100
                optimize-interval 10
                optimize-burn-in  20
                num-threads       (.availableProcessors (Runtime/getRuntime))
                random-seed       -1 }} parameters]
    (doto (ParallelTopicModel. num-topics)
      (.addInstances (make-instance-list documents))
      (.setNumIterations num-iter)
      (.setOptimizeInterval optimize-interval)
      (.setBurninPeriod optimize-burn-in)
      (.setNumThreads num-threads)
      (.setRandomSeed random-seed)
      .estimate)))
