(ns tools.topic-model.protocol)


(defprotocol Pipeline
  (extract [this args]
    "Extracts data from a source. Returns a vector: [id string]")
  (process [this args data]
    "Process data and return term vectors. Return a vector: [id [term-vector]]
     MUST be same order as result of extract."))
