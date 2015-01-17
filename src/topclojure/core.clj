(ns topclojure.core
  (:gen-class)
  (:require [net.cgrand.enlive-html :as enlive]
            [selmer.parser :as selmer]))

(defn fetch-html
  [url]
  (enlive/html-resource (java.net.URL. url)))

(defn fetch-selection
  [html selector]
  (apply enlive/text (enlive/select html selector)))

(defn fetch-problem
  [html]
  (let [selector [:td.statText :> :a]]
    (fetch-selection html selector)))

(defn fetch-class
  [html]
  (let [selector
        [:td.statText :> :table :> (enlive/nth-child 1) :> (enlive/nth-child 2)]]
    (fetch-selection html selector)))

(defn fetch-function
  [html]
  (let [selector
        [:td.statText :> :table :> (enlive/nth-child 2) :> (enlive/nth-child 2)]]
    (fetch-selection html selector)))

(defn fetch-signature
  [html]
  (let [selector
        [:td.statText :> :table :> (enlive/nth-child 5) :> (enlive/nth-child 2)]]
    (fetch-selection html selector)))

(defn retrieve-match
  [problem]
  (re-find #"\d{3}(?:\.\d)?" problem))

(defn retrieve-parameters
  [signature]
  (re-seq #"[^\s,)(]+(?=[,)])" signature))

(defn fetch-ios
  [html]
  (let [selector [:pre]]
    (map enlive/text (enlive/select html selector))))

(defn pack-ios
  [ios parameter-count]
  (partition (inc parameter-count) ios))

(defn retrieve-ios [subject]
  (let [replacement-pair [[#"\{" "["]
                          [#"\}" "]"]
                          [#"\," " "]
                          [#"^Returns: " ""]]]
    (reduce #(apply clojure.string/replace %1 %2) (str subject) replacement-pair)))

(defn consolidate-ios
  [ios-raw, signature]
  (let [parameter-count (count (retrieve-parameters signature))]
    (pack-ios (map retrieve-ios ios-raw) parameter-count)))

(def directory-path
  ((comp first read-string slurp) "path"))

(def directory
  (re-find #"[^/]*$" directory-path))

(defn retrieve-file-path
  [match class]
  (str directory-path "/Srm" match class ".clj"))

(defn -main
  [url]
  (let [html (fetch-html url)
        match (retrieve-match (fetch-problem html))
        class (fetch-class html)
        function (fetch-function html)
        signature (fetch-signature html)
        parameters (retrieve-parameters signature)
        ios (consolidate-ios (fetch-ios html) signature)
        timestamp (quot (System/currentTimeMillis) 1000)
        template (selmer/render-file "clojure.tmpl"
                                     {:directory  directory
                                      :match      match
                                      :class      class
                                      :function   function
                                      :parameters parameters
                                      :ios        ios
                                      :timestamp  timestamp})]
    (spit (retrieve-file-path match class) template)
    ))
