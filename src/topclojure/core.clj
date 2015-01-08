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
  (html/texts (html/select (fetch-html url) function-selector)))

(def function-selector
  [:td.statText :> :table :> (html/nth-child 5) :> (html/nth-child 2)])


(defn fetch-html
  [url]
  (html/html-resource (java.net.URL. url)))
