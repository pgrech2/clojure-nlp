(defproject clojure-nlp "0.1.0-SNAPSHOT"
  :description "Library to experiment with NLP in Clojure"
  :dependencies [[org.clojure/clojure "1.9.0-alpha16"]

                 ;; Clojure Community Libraries
                 [com.stuartsierra/component "0.3.2"]
                 [com.stuartsierra/frequencies "0.1.0"]
                 [com.taoensso/timbre "4.7.4"]

                 ;; NLP Libraries
                 [org.clojurenlp/core "3.7.0"]
                 [clojure-opennlp "0.4.0"]

                 ;; Topic Modeling
                 [cc.mallet/mallet "2.0.8"]

                 ;; SVM
                 [tw.edu.ntu.csie/libsvm "3.17"]]
  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "0.2.11"]
                                  [com.stuartsierra/component.repl "0.2.0"]
                                  [clojure-data "0.1.0-SNAPSHOT"]]
                   :source-paths ["dev"]}})
