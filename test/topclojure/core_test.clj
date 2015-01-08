(ns topclojure.core-test
  (:require [clojure.test :refer :all]
            [topclojure.core :refer :all]))

(deftest test-retrieve-function
  (is (= "train" (retrieve-function "int train(int[] attributes)"))))