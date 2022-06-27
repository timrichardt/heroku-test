(require
 '[babashka.curl :as curl]
 '[cheshire.core :as json])

;; ----------------------------------------
;; Helpers

(defmacro deftest
  [test-name expr]
  `(if ~expr
     (println (str "'" ~(name test-name) "' succeeded"))
     (do
       (println (str "'" ~(name test-name) "' failed"))
       (System/exit 1))))

(defn post
  [body]
  (update (curl/post
           "https://morning-inlet-68068.herokuapp.com"
           {:headers {"Content-Type" "application/json"}
            :body    (json/generate-string body)
            :throw   false})
          :body
          json/parse-string))


;; ----------------------------------------
;; Tests

(deftest valid-input
  (let [response (post {"address" {"values" [1 2 3]}})]
    (and (= (:status response))
         (= (:body response)
            {"result" 6}))))


(deftest empty-input
  (let [response (post {})]
    (= (:status response) 400)))


(deftest malformed-input
  (let [response (post {"address" {"values" [1 "2" 3]}})]
    (= (:status response) 400)))
