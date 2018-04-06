(ns dev
  (:require [clojure.set :as set]
            [clojure.string :as string]
            [clojure.tools.namespace.repl :refer [refresh refresh-all clear]]
            [com.stuartsierra.component :as component]
            [com.stuartsierra.component.repl :refer [reset set-init start stop system]]
            [clojure-nlp]))

;; Do not try to load source code from 'resources' directory
(clojure.tools.namespace.repl/set-refresh-dirs "dev" "src" "test")

(defn dev-system
  "Constructs a system map suitable for interactive development."
  []
  (component/system-map
   ;; TODO
   ))

(set-init (fn [_] (dev-system)))


;; Data analysis
;; http://ana.cachopo.org/datasets-for-single-label-text-categorization
