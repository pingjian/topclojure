(ns topclojure.core
  (:gen-class)
  (:require [net.cgrand.enlive-html :as enlive]
            [selmer.parser :as selmer]
            [environ.core :as environ]
            [clojure.java.io :as io]))

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
  (let [parameter-count ((comp count retrieve-parameters) signature)]
    (pack-ios (map retrieve-ios ios-raw) parameter-count)))

(def settings-path-default
  "settings.edn")

(def settings-path-custom
  (environ/env :settings))

(defn settings-path
  [custom default]
  (if (nil? custom)
    default
    custom))

(def settings
  ((comp read-string slurp) (settings-path settings-path-custom settings-path-default)))

(defn retrieve-directory-name
  [directory-path]
  (re-find #"[^/]*$" directory-path))

(defn retrieve-file-path
  [directory-path match class]
  (str directory-path "/Srm" match class ".clj"))

(defn retrieve-template-filename
  [language]
  (str language ".tmpl"))

(defn -main
  [url]
  (let [directory-path (settings :path)
        directory-name (retrieve-directory-name (settings :path))
        html (fetch-html url)
        match ((comp retrieve-match fetch-problem) html)
        class (fetch-class html)
        function (fetch-function html)
        signature (fetch-signature html)
        parameters (retrieve-parameters signature)
        ios (consolidate-ios (fetch-ios html) signature)
        timestamp (quot (System/currentTimeMillis) 1000)
        file-path (retrieve-file-path directory-path match class)
        template-filename (retrieve-template-filename (settings :language))
        template (selmer/render-file template-filename
                                     {:directory  directory-name
                                      :match      match
                                      :class      class
                                      :function   function
                                      :parameters parameters
                                      :ios        ios
                                      :timestamp  timestamp})]
    (spit file-path template)
    ))
