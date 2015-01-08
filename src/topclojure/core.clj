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

(defn fetch-ios
  [url]
  (let [io-selector [:pre]
        parameter-count (count (fetch-signature url))]
    (partition (inc parameter-count)
               (map html/text (html/select (fetch-html url) io-selector)))))

(defn replace-multiple [subject & [replacements]]
  (let [replacement-pair (partition 2 replacements)]
    (reduce #(apply clojure.string/replace %1 %2) (str subject) replacement-pair)))

(defn prettify-input
  [input]
  (let [replacement-pair [#"\{" "["
                          #"\}" "]"
                          #"\," ""]]
    (replace-multiple input replacement-pair)))

(defn -main
  [url]
  (let [function (retrieve-function (fetch-signature url))
        parameters (retrieve-parameters (fetch-signature url))]
    (selmer/render-file "template.clj"
                        {:function   function
                         :parameters parameters})))

