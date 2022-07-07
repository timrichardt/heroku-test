(defproject clojure-getting-started "1.0.0-SNAPSHOT"
  :description "heroku test"
  :url "https://morning-inlet-68068.herokuapp.com/"
  :license {:name "Eclipse Public License v1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [compojure "1.6.1" :exclusions [ring/ring-core]]
                 [ring "1.9.5"]
                 [ring/ring-json "0.5.1"]
                 [environ "1.1.0"]
                 [metosin/malli "0.8.8"]
                 [criterium "0.4.6"]]
  :min-lein-version "2.0.0"
  :plugins [[environ/environ.lein "0.3.1"]]
  :hooks [environ.leiningen.hooks]
  :uberjar-name "heroku-test.jar"
  :profiles {:production {:env {:production true}}})
