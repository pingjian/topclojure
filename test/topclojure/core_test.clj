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

(deftest test-prettify-ios
  (are [x y] (= x y)
             "[1 2]" (prettify-io "{1,2}")
             "1" (prettify-io "1")
             "3" (prettify-io "Returns: 3")))

(deftest test-pack-ios
  (are [x y] (= x y)
             [["[1 2 3]" "3"] ["[5 5]" "0"]] (pack-ios '("[1 2 3]" "3" "[5 5]" "0") 1)))

(deftest test-pack-inputs
  (are [x y] (= x y)
             [[["1" "2"] "3"] [["5" "5"] "10"]] (pack-inputs [["1" "2" "3"] ["5" "5" "10"]])))
