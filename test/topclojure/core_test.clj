(ns topclojure.core-test
  (:require [clojure.test :as test]
            [topclojure.core :as tc]))

(test/deftest test-retrieve-parameters
  (test/are [x y] (= x y)
             '("attributes") (tc/retrieve-parameters "int train(int[] attributes)")
             '("K" "danceCost") (tc/retrieve-parameters
                                  "int minimum(int K, int[] danceCost)")))

(test/deftest test-retrieve-ios
  (test/are [x y] (= x y)
             "A" (tc/retrieve-ios "A")
             "[]" (tc/retrieve-ios "{}")))

(test/deftest test-pack-ios
  (test/are [x y] (= x y)
             [["[1 2 3]" "3"] ["[5 5]" "0"]] (tc/pack-ios '("[1 2 3]" "3" "[5 5]" "0") 1)))

