(ns dev
  (:require [clojure.set :as cset]
            [clojure.string :as cstr]
            [clojure.java.io :as jio]
            [clojure.tools.namespace.repl :refer [refresh refresh-all clear]]
            [com.stuartsierra.component :as component]
            [com.stuartsierra.component.repl :refer [reset set-init start stop system]]
            [taoensso.timbre :as log]
            [tools.data :as d]
            [tools.io :as io]
            [model.tfidf :as tfidf]
            [model.topic-model :as tm]))

;; Do not try to load source code from 'resources' directory
(clojure.tools.namespace.repl/set-refresh-dirs "dev" "src" "test")

(def data-file "data/r8-train-all-terms.txt")

(defn dev-system
  "Constructs a system map suitable for interactive development."
  []
  (component/system-map
   :data (d/loader {:file   data-file
                    :parse  (partial io/read-file #"\t")})))

(set-init (fn [_] (dev-system)))

(def stopwords #{"a" "all" "and" "any" "are" "is" "in" "of" "on"
                 "or" "our" "so" "this" "the" "that" "to" "we"})

(defn my-transform
  [[classification text]]
  {:classification classification
   :text           text})

(defn my-tokenizer [{:keys [text] :as record}]
  (assoc record :term-vector
         (-> text
             (cstr/lower-case)
             (->> (re-seq #"[0-9a-zA-Z ]")
                  (apply str))
             (cstr/split #" "))))

(defn my-analyzer [{:keys [term-vector] :as record}]
  (assoc record :term-vector
         (->> term-vector
              (set)
              (remove stopwords)
              (into []))))

(defn r8 [sys]
  (->> (d/records (:data sys) my-transform my-tokenizer my-analyzer)
       (map-indexed (fn [i {:keys [text term-vector]}]
                      [i term-vector]))))


;; Example of TFIDF
;; (def tfidf (tfidf/train (r8 system)))

;; Example of LDA Topic Model on R8 Corpus:
;; (def lda (tm/train (r8 system)))


;; Data analysis
;; http://ana.cachopo.org/datasets-for-single-label-text-categorization
