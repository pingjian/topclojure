(ns topclojure.core
  (:require [net.cgrand.enlive-html :as html]
            [selmer.parser :as selmer]))

(defn fetch-html
  [url]
  (html/html-resource (java.net.URL. url)))

(defn retrieve-class
  [url]
  (let [function-selector
        [:td.statText :> :table :> (html/nth-child 1) :> (html/nth-child 2)]]
    (apply html/text (html/select (fetch-html url) function-selector))))

(defn fetch-signature
  [url]
  (let [function-selector
        [:td.statText :> :table :> (html/nth-child 5) :> (html/nth-child 2)]]
    (apply html/text (html/select (fetch-html url) function-selector))))

(defn retrieve-function
  [signature]
  (let [pattern (re-pattern #"\s([^(]+)\(")
        matcher (re-matcher pattern signature)]
    (second (re-find matcher))))

(defn retrieve-parameters
  [signature]
  (re-seq #"[^\s,)(]+(?=[,)])" signature))

(defn fetch-ios
  [url]
  (let [io-selector [:pre]]
    (map html/text (html/select (fetch-html url) io-selector))))

(defn pack-ios
  [ios parameter-count]
  (partition (inc parameter-count) ios))

(defn prettify-ios [subject]
  (let [replacement-pair [[#"\{" "["]
                          [#"\}" "]"]
                          [#"\," " "]
                          [#"^Returns: " ""]]]
    (reduce #(apply clojure.string/replace %1 %2) (str subject) replacement-pair)))

(defn retrieve-ios
  [ios-raw, signature]
  (let [parameter-count (count (retrieve-parameters signature))]
    (pack-ios (map prettify-ios ios-raw) parameter-count)))

(defn -main
  [url]
  (let [signature (fetch-signature url)
        function (retrieve-function signature)
        parameters (retrieve-parameters signature)
        ios (retrieve-ios (fetch-ios url) signature)
        template (selmer/render-file "template.clj"
                        {:function   function
                         :parameters parameters
                         :ios        ios})]
    (spit "check.clj" template)
    ))
