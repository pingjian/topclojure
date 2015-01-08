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
    (apply html/text (html/select (fetch-html url) function-selector))))

(defn retrieve-function
  [signature]
  (let [pattern (re-pattern #"\s([^(]+)\(")
        matcher (re-matcher pattern signature)]
    (nth (re-find matcher) 1)))

(defn retrieve-parameters
  [signature]
  (re-seq #"[^\s,)(]+(?=[,)])" signature))

(defn -main
  [url]
  (let [function (retrieve-function (fetch-signature url))
        parameters (retrieve-parameters (fetch-signature url))]
    (selmer/render-file "template.clj"
                        {:function function
                        :parameters parameters})))

