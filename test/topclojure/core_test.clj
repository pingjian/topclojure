(ns topclojure.core-test
  (:require [clojure.test :refer :all]
            [topclojure.core :refer :all]))

(deftest test-retrieve-parameters
  (are [x y] (= x y)
             '("attributes") (retrieve-parameters "int train(int[] attributes)")
             '("K" "danceCost") (retrieve-parameters
                                  "int minimum(int K, int[] danceCost)")))

(deftest test-retrieve-ios
  (are [x y] (= x y)
             "A" (retrieve-ios "A")
             "[]" (retrieve-ios "{}")))

(deftest test-pack-ios
  (are [x y] (= x y)
             [["[1 2 3]" "3"] ["[5 5]" "0"]] (pack-ios '("[1 2 3]" "3" "[5 5]" "0") 1)))

