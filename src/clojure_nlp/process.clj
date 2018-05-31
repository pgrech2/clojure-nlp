(ns clojure-nlp.process
  (:import (org.apache.commons.lang3 StringEscapeUtils)))

(defn html-unescape
  "give a string `s`, returns the same string with all html characters and tags unescaped"
  [^String s]
  (StringEscapeUtils/unescapeHtml4 s))
