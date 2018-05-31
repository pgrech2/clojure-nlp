(ns clojure-nlp.svm
  (:refer-clojure :exclude (replace))
  (:import [libsvm svm_node svm_parameter svm_problem svm])
  (:require [clojure.string :as cstr]))


;; Thanks to:
;; https://github.com/r0man/svm-clj/blob/master/src/svm/core.clj

(defn get-kernel
  [kernel]
  (get {:linear       svm_parameter/LINEAR
        :poly         svm_parameter/POLY
        :pre-computed svm_parameter/PRECOMPUTED
        :rbf          svm_parameter/RBF
        :sigmoid      svm_parameter/SIGMOID} kernel))

(defn get-svm
  [svm]
  (get {:c-svc       svm_parameter/C_SVC
        :epsilon-svr svm_parameter/EPSILON_SVR
        :nu-svc      svm_parameter/NU_SVC
        :nu-svr      svm_parameter/NU_SVR
        :one-class   svm_parameter/ONE_CLASS} svm))

(defn feature-counts
  "Return set of unique feature counts for all records."
  [records]
  (set (map (comp count last) records)))

(defn max-features
  "Return maximum feature count for all records"
  [records]
  (apply max (feature-counts records)))

(defn make-node
  "Make a LibSVM node."
  [index value]
  (let [node (svm_node.)]
    (set! (. node index) index)
    (set! (. node value) value)
    node))

(defn feature-node
  "Make a seq of LibSVM nodes."
  [[label features]]
  ;; HACK
  (if (map? features)
    (map (partial apply make-node) features)
    (map-indexed (fn [i f] (apply make-node [i f])) features)))

(defn svm-params
  "Return SVM parameters."
  [max-features parameters]
  (let [{:keys [kernel-type svm-type]} parameters
        all-params (cond-> (merge {:C            1
                                   :cache-size   100
                                   :coef0        0
                                   :degree       3
                                   :eps          1e-3
                                   :gamma        0
                                   :kernel-type  (get-kernel :rbf)
                                   :nr-weight    0
                                   :nu           0.5
                                   :p            0.1
                                   :probability  0
                                   :shrinking    1
                                   :svm-type     (get-svm :c-svc)
                                   :weight       (double-array 0)
                                   :weight-label (int-array 0)}
                                  parameters)
                     kernel-type (assoc :kernel-type
                                        (get-kernel kernel-type))
                     svm-type    (assoc :svm-type
                                        (get-svm svm-type)))
        svm-params (svm_parameter.)]
    (doseq [[k v] all-params]
      (clojure.lang.Reflector/setInstanceField
       svm-params (cstr/replace (str k) #"-" "_") v))
    (set! (.gamma svm-params)
          (/ 1.0 max-features))
    svm-params))


(defn svm-problem
  "Make a LibSVM problem from `dataset`."
  [records]
  (let [problem (svm_problem.)]
    (set! (. problem l) (count records))
    (set! (. problem y) (-> (map first records)
                            (double-array)))
    (set! (. problem x) (->> (map feature-node records)
                             (map into-array)
                             (into-array)))
    problem))

(defn train
  "Train a SVM model with `records` according to optional `parameters`
   Accepts either vector of features or spare-map of features"
  ([records]
   (train records nil))
  ([records parameters]
   (let [svm-params (svm-params (max-features records)
                                parameters)
         problem    (svm-problem records)]
     (svm/svm_check_parameter problem svm-params)
     (svm/svm_train problem svm-params))))

(defn predict
  "Predict the label for `features` with `model`."
  [model features]
  (->> (feature-node [nil features])
       (into-array)
       (svm/svm_predict model)))
