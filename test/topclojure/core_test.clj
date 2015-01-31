(ns topclojure.core-test
  (:require [clojure.test :as test]
            [topclojure.core :as tc]
            [environ.core :as environ]))

(test/deftest test-get-parameters
  (test/are [x y] (= x y)
                  '("attributes") (tc/get-parameters "int train(int[] attributes)")
                  '("K" "danceCost") (tc/get-parameters
                                       "int minimum(int K, int[] danceCost)")))

(test/deftest test-get-ios
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

(test/deftest test-get-settings-path
  (test/are [x y] (= x y)
                  (tc/get-settings-path "custom.edn" "default.edn") "custom.edn"
                  (tc/get-settings-path nil "default.edn") "default.edn"))

(test/deftest test-get-directory
  (test/is (tc/get-directory-name "path/to/dummy") "dummy"))

(test/deftest test-get-template-filename
  (test/is (tc/get-template-filename "clojure") "clojure.tmpl"))