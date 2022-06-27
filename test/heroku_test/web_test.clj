(ns heroku-test.web-test
  (:require [clojure.test :refer :all]
            [heroku-test.web :as web]))


(deftest hash
  (is (= (web/hash [12 3 3]) 9)))
