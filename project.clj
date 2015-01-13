(defproject topclojure "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/math.combinatorics "0.0.8"]
                 [org.clojure/math.numeric-tower "0.0.4"]
                 [enlive "1.1.5"]
                 [selmer "0.7.9"]]
  :main ^:skip-aot topclojure.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
