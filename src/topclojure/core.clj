(ns topclojure.core
  (:require [net.cgrand.enlive-html :as html]
            [selmer.parser :as selmer]))

(defn fetch-html
  [url]
  (html/html-resource (java.net.URL. url)))

(defn fetch-signature
  [url]
  (let [function-selector
        [:td.statText :> :table :> (html/nth-child 5) :> (html/nth-child 2)]]
    (html/texts (html/select (fetch-html url) function-selector))))

(defn retrieve-function
  [signature]
  (let [pattern (re-pattern #"\s([^(]+)\(")
        matcher (re-matcher pattern (str signature))]
    (nth (re-find matcher) 1)))

(defn retrieve-parameters
  [signature]
  (re-seq #"[^\s,)(]+(?=[,)])" (str signature)))

(defn count-parameters
  [signature]
  (count (retrieve-parameters signature)))

(defn fetch-ios
  [url]
  (let [io-selector [:pre]
        parameter-count (count-parameters (fetch-signature url))]
    (partition parameter-count
               (map html/text (html/select (fetch-html url) io-selector)))))

(defn -main
  [url]
  (let [function (retrieve-function (fetch-signature url))
        parameters (retrieve-parameters (fetch-signature url))]
    (selmer/render-file "template.clj"
                        {:function   function
                         :parameters parameters})))

