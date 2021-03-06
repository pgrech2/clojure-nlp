(ns dev
  (:require [clojure.set :as cset]
            [clojure.string :as cstr]
            [clojure.java.io :as jio]
            [clojure.spec.alpha :as s]
            [clojure.tools.namespace.repl :refer [refresh refresh-all clear]]
            [com.stuartsierra.component :as component]
            [com.stuartsierra.component.repl :refer [reset set-init start stop system]]
            [com.stuartsierra.frequencies :as freq]
            [taoensso.timbre :as log]

            [clojure-nlp.svm :as svm]
            [clojure-nlp.text :as text]
            [clojure-nlp.topic-model :as tm]

            [clojure-data.io :as io]
            [clojure-data.load :as ld]
            [clojure-data.helpers :as help]))

;; Do not try to load source code from 'resources' directory
(clojure.tools.namespace.repl/set-refresh-dirs "dev" "src" "test")

(def data-file "data/r8-train-all-terms.txt")

(defn dev-system
  "Constructs a system map suitable for interactive development."
  []
  (component/system-map
   :data (ld/loader {:file data-file
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
         (->> (set term-vector)
              (remove stopwords)
              (into []))))

(defn r8 [sys]
  (->> (ld/records (:data sys) my-transform my-tokenizer my-analyzer)
       (map-indexed (fn [i {:keys [text term-vector]}]
                      [i term-vector]))))

(defn records [sys]
  (ld/records (:data sys)))


;; Example of TFIDF
;; (def tfidf (text/tfidf (r8 system)))

;; Example of LDA Topic Model on R8 Corpus:
;; (def lda (tm/train (r8 system)))


;; Data analysis
;; http://ana.cachopo.org/datasets-for-single-label-text-categorization


;; SVM Example

;; (defn r8 [sys]
;;   (->> (d/records (:data sys) my-transform my-tokenizer my-analyzer)
;;        (map-indexed (fn [i {:keys [text term-vector]}]
;;                       [i term-vector]))))



(defn default-config [config]
  (when (clojure.spec.alpha/valid? ::config config)
    true))

(def temp (default-config {:a "a"}))

(s/def ::config map?)


(defmulti greeting
  "This is another way"
  "language"
  ;;(fn[x] (x "language"))
  )

                                        ;params is not used, so we could have used [_]
(defmethod greeting "English" [params]
  "Hello!")

(defmethod greeting "French" [params]
  "Bonjour!")
