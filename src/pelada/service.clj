(ns pelada.service
  (:require [clojure.java.io :as io])
  (:require [clojure.java.shell :as shell])
  (:require [clojure.string :as str])
  (:require [cheshire.core :as json]))

(defn- read-json
  [path]
  (json/parse-string (slurp path) true))

(defn get-setup 
  []
  (read-json (io/resource "setup.json")))

(defn get-pelada
  []
  (read-json "pelada.json"))

(defn- execute-shell-command
  [command]
  (let [result (shell/sh command)]
    (:out result)))

(defn ensure-major-dependency 
  []
  (let [output (execute-shell-command "pelada-parser")]
    (if (not (str/includes? output "command not found"))
      (do 
        (println "pelada-parser is available.")
        (System/exit 0))
      (do
        (println "[Error]: pelada-parser is not available.")
        (System/exit 1)))))
