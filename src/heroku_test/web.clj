(ns heroku-test.web
  (:require [compojure.core :refer [defroutes POST ANY]]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [environ.core :refer [env]]))


;; ----------------------------------------
;; Hashing function

(defn- int->digit-sequence
  "Takes integer `number` and returns sequence of its digits in base 10."
  ([number]
   {:pre (int? number)}
   (int->digit-sequence number []))
  ([number digits]
   (if (zero? number)
     (reverse digits)
     (let [remainder (mod number 10)]
       (recur (/ (- number remainder) 10) (conj digits remainder))))))


(defn- int->digit-sequence-2
  "Takes integer `number` and returns sequence of its digits in base 10.

  Another way to `int->digit-sequence`, seems to be faster.

  (criterium.core/bench (int->digit-sequence-2 123456789012345))
               Execution time mean : 57.865278 ns

  (criterium.core/bench (int->digit-sequence 123456789012345))
               Execution time mean : 2.482377 µs"
  [number]
  (->> (str number)
       (map #(Integer/parseInt (str %)))))


(defn hash
  "Compute some hash of integer sequence `values`.

  The hash is the digit sum of the sum of `values`."
  [values]
  (->> values
       (reduce +)
       int->digit-sequence-2
       (reduce +)))


;; ----------------------------------------
;; Request handlers

(defn NOT_FOUND
  "Status 404 Not Found response."
  [_]
  {:status 404
   :headers {"Content-Type" "text/plain"}
   :body "404"})


(defn BAD_REQUEST
  "Status 400 Bad Request response with body `error`."
  [error]
  {:status  400
   :headers {}
   :body    error})


(defn OK
  "Status 200 OK response with body `body`."
  [body]
  {:status  200
   :headers {}
   :body    body})


(defn hash-handler
  "Handles hash input validation and computation."
  [request]  
  (let [values (get-in request [:body "address" "values"])]
    (cond (not values)
          (BAD_REQUEST {:error "Invalid request input: no key address.values."})

          (not (every? int? values))
          (BAD_REQUEST {:error          "Invalid request input: address.input must be an array of integers."
                        :address.values values})

          :else
          (OK {:result (hash values)}))))


;; ----------------------------------------
;; Routing

(defroutes service
  (POST "/" [] (->> hash-handler
                    wrap-json-body
                    wrap-json-response))
  (ANY "*" [] NOT_FOUND))


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
