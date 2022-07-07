(ns heroku-test.web
  (:require [compojure.core :refer [defroutes POST ANY]]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.util.response :as response]
            [environ.core :refer [env]]
            [malli.core :as malli]
            [malli.util :as malli-util]))


;; ----------------------------------------
;; Checksum

(defn- digit-sum
  "Takes integer `number` and returns its base 10 digit sum."
  ([number] (digit-sum number 0))
  ([number result]
   (if (zero? number)
     result
     (let [remainder (mod number 10)]
       (recur (/ (- number remainder) 10) (+ result remainder))))))


(defn checksum
  "Compute a checksum of integer sequence `values`.

  The checksum is the digit sum of the sum of `values`."
  [values]
  (->> (reduce + values)
       digit-sum))


;; ----------------------------------------
;; Request handlers

(defn checksum-handler
  "Handles checksum input validation and computation."
  [{:keys [body] :as request}]
  (let [RequestBody [:map ["address" [:map ["values" [:vector int?]]]]]]
    (if (malli/validate RequestBody body)
      (let [values (get-in body ["address" "values"])]
        (response/response {:result (checksum values)}))
      (response/bad-request (malli-util/explain-data RequestBody body)))))


;; ----------------------------------------
;; Routing

(defroutes service
  (POST "/" [] (-> checksum-handler
                   wrap-json-response
                   wrap-json-body))
  (ANY "*" [] (response/not-found nil)))


;; ----------------------------------------
;; Initialization

(defn -main
  ""
  [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty service {:port port :join? false})))


(comment
  ;; For interactive development:
  (def server (-main))
  (.stop server))
