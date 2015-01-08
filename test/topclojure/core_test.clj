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

(deftest test-count-parameters
  (are [x y] (= x y)
        1 (count-parameters "int train(int[] attributes)")
        2 (count-parameters "int minimum(int K, int[] danceCost)")))
