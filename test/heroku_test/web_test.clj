(ns heroku-test.web-test
  (:require [clojure.test :refer :all]
            [heroku-test.web :as web]))


(deftest checksum
  (is (= (web/checksum [12 3 3]) 9)))
