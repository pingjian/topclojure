(ns topclojure.core-test
  (:require [clojure.test :as test]
            [topclojure.core :as tc]
            [environ.core :as environ]))

(test/deftest test-retrieve-parameters
  (test/are [x y] (= x y)
                  '("attributes") (tc/retrieve-parameters "int train(int[] attributes)")
                  '("K" "danceCost") (tc/retrieve-parameters
                                       "int minimum(int K, int[] danceCost)")))

(test/deftest test-retrieve-ios
  (let [replacement-pair [[#"\{" "["]
                          [#"\}" "]"]]]
    (test/are [x y] (= x y)
                    "A" ((tc/make-retrieve-ios replacement-pair) "A")
                    "[]" ((tc/make-retrieve-ios replacement-pair) "{}"))))

(test/deftest test-pack-ios
  (test/are [x y] (= x y)
                  [["[1 2 3]" "3"] ["[5 5]" "0"]] (tc/pack-ios '("[1 2 3]" "3" "[5 5]" "0") 1)))

(test/deftest test-environ-env
  (test/is (environ/env :settings) "settings/clojure.edn"))

(test/deftest test-retrieve-settings-path
  (test/are [x y] (= x y)
                  (tc/retrieve-settings-path "custom.edn" "default.edn") "custom.edn"
                  (tc/retrieve-settings-path nil "default.edn") "default.edn"))

(test/deftest test-retrieve-directory
  (test/is (tc/retrieve-directory-name "path/to/dummy") "dummy"))

(test/deftest test-retrieve-template-filename
  (test/is (tc/retrieve-template-filename "clojure") "clojure.tmpl"))