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

(deftest test-prettify-input
  )