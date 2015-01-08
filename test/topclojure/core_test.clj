(ns topclojure.core-test
  (:require [clojure.test :refer :all]
            [topclojure.core :refer :all]))

(deftest test-retrieve-function
  (is (= "train" (retrieve-function "int train(int[] attributes)"))))

(deftest test-retrieve-parameters
  (are [x y] (= x y)
             '("attributes") (retrieve-parameters "int train(int[] attributes)")
             '("K" "danceCost") (retrieve-parameters
                                 "int minimum(int K, int[] danceCost)")))

(deftest test-replace-multiple
  (are [x y] (= x y)
             "B" (replace-multiple "A" [#"A" "B"])
             "C" (replace-multiple "C" [#"B" "B"])))

(deftest test-prettify-vector
  (are [x y] (= x y)
             "[1 2]" (prettify-vector "{1,2}")
             "1" (prettify-vector "1")))

(deftest test-prettify-output
  (is (= "3" (prettify-output "Returns: 3"))))

