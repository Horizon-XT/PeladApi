(ns pelada.controller
;  (:require [clojure.string :as str])
  (:require [pelada.model :as model])
  (:require [pelada.service :as service]))
;  (:require [clojure.core.match :as matcher]))
  
(defn get-setup
  []
  (service/get-setup))

(defn get-pelada
  []
  (let [pelada (service/get-pelada)
        goalkeepers (:goalkeepers pelada)
        players (:players pelada)
        guests (:guests pelada)
        kids (:kids pelada)]
    {:goalkeeper goalkeepers
     :players players
     :guests guests
     :kids kids}))

(defn ensure-major-dependency
  []
  (service/ensure-major-dependency))

(defn generate-weekly-list
  "Returns the weekly list with the updated date"
  []
  (println (model/weekly-list get-setup)))

(defn make-teams
  "Generate the 3 teams"
  [pelada]
  (model/team-maker pelada))

(defn print-teams
  "Print the 3 teams formatted"
  [[t1 t2 t3]]
  (model/print-teams [t1 t2 t3]))

(defn print-formatted-list-to-concierge
  "Print the formatted list to concierge"
  [pelada]
  (model/concierge-format pelada))
