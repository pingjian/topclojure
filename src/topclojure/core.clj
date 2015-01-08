(ns topclojure.core
  (:require [net.cgrand.enlive-html :as html]))

(defn -main
  [& args]
  (println "Hello, World!"))

(defn retrieve-function
  [signature]
  (let [pattern (re-pattern #"\s([^(]*)\(")
        matcher (re-matcher pattern signature)]
    (nth (re-find matcher) 1)))

(defn fetch-signature
  [url]
  (let [function-seletor
        [:td.statText :> :table :> (html/nth-child 5) :> (html/nth-child 2)]]
    (html/texts (html/select (fetch-html url) function-selector))))

(defn fetch-html
  [url]
  (html/html-resource (java.net.URL. url)))
