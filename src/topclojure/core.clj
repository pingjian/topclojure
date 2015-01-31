(ns topclojure.core
  (:gen-class)
  (:require [net.cgrand.enlive-html :as enlive]
            [selmer.parser :as selmer]
            [environ.core :as environ]
            [clojure.java.io :as io]))

(defn get-html
  [url]
  (enlive/html-resource (java.net.URL. url)))

(defn get-selection
  [html selector]
  (apply enlive/text (enlive/select html selector)))

(defn get-problem
  [html]
  (let [selector [:td.statText :> :a]]
    (get-selection html selector)))

(defn get-class
  [html]
  (let [selector
        [:td.statText :> :table :> (enlive/nth-child 1) :> (enlive/nth-child 2)]]
    (get-selection html selector)))

(defn get-function
  [html]
  (let [selector
        [:td.statText :> :table :> (enlive/nth-child 2) :> (enlive/nth-child 2)]]
    (get-selection html selector)))

(defn get-signature
  [html]
  (let [selector
        [:td.statText :> :table :> (enlive/nth-child 5) :> (enlive/nth-child 2)]]
    (get-selection html selector)))

(defn get-match
  [problem]
  (re-find #"\d{3}(?:\.\d)?" problem))

(defn get-parameters
  [signature]
  (re-seq #"[^\s,)(]+(?=[,)])" signature))

(defn get-ios
  [html]
  (let [selector [:pre]]
    (map enlive/text (enlive/select html selector))))

(defn pack-ios
  [ios parameter-count]
  (partition (inc parameter-count) ios))

(def replacement-pair-clojure
  [[#"\{" "["]
   [#"\}" "]"]
   [#"\," " "]
   [#"^Returns: " ""]]
  )

(defn get-replacement-pair
  [language]
  ({"clojure" replacement-pair-clojure} language))

(defn make-retrieve-ios
  [replacement-pair]
  (fn [subject] (reduce #(apply clojure.string/replace %1 %2) (str subject) replacement-pair)))

(defn consolidate-ios
  [ios-raw, signature replacement-pair]
  (let [parameter-count ((comp count get-parameters) signature)
        retrieve-ios (make-retrieve-ios replacement-pair)]
    (pack-ios (map retrieve-ios ios-raw) parameter-count)))

(def settings-path-default
  "settings.edn")

(def settings-path-custom
  (environ/env :settings))

(defn get-settings-path
  [custom default]
  (if (nil? custom)
    default
    custom))

(defn get-settings
  [settings-path]
  ((comp read-string slurp) settings-path))

(defn get-directory-name
  [directory-path]
  (re-find #"[^/]*$" directory-path))

(defn get-file-path
  [directory-path match class]
  (str directory-path "/Srm" match class ".clj"))

(defn get-template-filename
  [language]
  (str language ".tmpl"))

(defn -main
  [url]
  (let [settings-path (get-settings-path settings-path-custom settings-path-default)
        settings (get-settings settings-path)
        directory-path (settings :path)
        directory-name (get-directory-name (settings :path))
        html (get-html url)
        match ((comp get-match get-problem) html)
        class (get-class html)
        function (get-function html)
        signature (get-signature html)
        parameters (get-parameters signature)
        replacement-pair (get-replacement-pair (settings :language))
        ios (consolidate-ios (get-ios html) signature replacement-pair)
        timestamp (quot (System/currentTimeMillis) 1000)
        file-path (get-file-path directory-path match class)
        template-filename (get-template-filename (settings :language))
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
