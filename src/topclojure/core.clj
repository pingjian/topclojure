(ns topclojure.core
  (:require [net.cgrand.enlive-html :as enlive]
            [selmer.parser :as selmer]))

(defn fetch-html
  [url]
  (enlive/html-resource (java.net.URL. url)))

(defn fetch-part
  [html selector]
  (apply enlive/text (enlive/select html selector)))

(defn fetch-class
  [html]
  (let [selector
        [:td.statText :> :table :> (enlive/nth-child 1) :> (enlive/nth-child 2)]]
    (fetch-part html selector)))

(defn fetch-signature
  [html]
  (let [selector
        [:td.statText :> :table :> (enlive/nth-child 5) :> (enlive/nth-child 2)]]
    (fetch-part html selector)))

(defn fetch-function
  [html]
  (let [selector
        [:td.statText :> :table :> (enlive/nth-child 2) :> (enlive/nth-child 2)]]
    (fetch-part html selector)))

(defn retrieve-parameters
  [signature]
  (re-seq #"[^\s,)(]+(?=[,)])" signature))

(defn fetch-ios
  [html]
  (let [io-selector [:pre]]
    (map enlive/text (enlive/select html io-selector))))

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
  (let [html (fetch-html url)
        signature (fetch-signature html)
        function (fetch-function html)
        parameters (retrieve-parameters signature)
        ios (retrieve-ios (fetch-ios html) signature)
        template (selmer/render-file "template.clj"
                        {:function   function
                         :parameters parameters
                         :ios        ios})]
    (spit "check.clj" template)
    ))
