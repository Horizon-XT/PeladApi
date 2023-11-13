(ns pelada.api
  (:require [clojure.core.match :as matcher])
  (:require [pelada.controller :as controller]))

(def commands-string
  "\n\t--validate-env - Check if pelada-parser is installed
  \t--weekly-list - Generate the weekly list to send to the group
  \t--make-teams - Team selection (3 of 5)
  \t--list-concierge - Format the list to send to the concierge
  \t--h - Print the helps menu (this one)")

(defn validate-environment
  []
  (controller/ensure-major-dependency))
  
(defn print-help-invalid-command
  []
  (println "Invalid command! Available ones:\n" commands-string))

(defn print-help-wrong-number-args
  []
  (println "Please give valid inputs:"
           "\n\n\tlein run <command> <filepath>" commands-string))

(defn team-maker-flow
  "Command flow to --make-teams"
  [] 
  (->> (controller/get-pelada)
       (controller/make-teams)
       (controller/print-teams)))

(defn concierge-list-flow
  "Command flow to --list-concierge"
  [] 
  (->> (controller/get-pelada)
       (controller/print-formatted-list-to-concierge)))

(defn generate-weekly-list
  "Command flow to --weekly-list"
  []
  (controller/generate-weekly-list))

(defn command-handler
  "Function that will handler the CLI input"
  [command filepath]
  (matcher/match [command]
           ["--validate-env"] (validate-environment)
           ["--weekly-list"] (generate-weekly-list)
           ["--make-teams"] (team-maker-flow)
           ["--list-concierge"] (concierge-list-flow)
           ["--server"] (println "Starting server...")
           ["--h"] (println commands-string)
           [_] (print-help-invalid-command)))
