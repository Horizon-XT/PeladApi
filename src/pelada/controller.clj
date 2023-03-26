(ns pelada.controller
  (:require [pelada.model :as model])
  (:require [clojure.string :as str])
  (:require [clojure.core.match :as matcher]))
  
; Parser Functions
(defn- remove-number-from-list
  "Remove the number of the player in the list"
  [string]
  (->> (str/split string #"\.")
       second))

(defn- sublist
  "The main function of the function get-set-list.
  Responsible for retrieve teh sublist and remove the head(set name)"
  ([lines-list from] (rest (subvec lines-list (.indexOf lines-list from) (- (count lines-list) 1)))) 
  ([lines-list from to] 
  (let [fromIndex (.indexOf lines-list from)
        toIndex (.indexOf lines-list to)]
    (->> (subvec lines-list fromIndex toIndex)
         (rest)
         (filter #(not= % ""))
         (map remove-number-from-list)))))

(defn- get-set-list
  "This function returns the sublist given the name of the set"
  [lines-list set-name]
  (matcher/match [set-name]
           [:goalkeepers] (sublist lines-list "Goleiros" "Jogadores")
           [:players] (sublist lines-list "Jogadores" "Suplentes")
           [:substitutes] (sublist lines-list "Suplentes" "Convidados")
           [:guests] (sublist lines-list "Convidados" "Comunicados")))

(defn parser
  "This function exists because even that the application know the format of the list
  it can't just trust in the user, so this function will create format that will be
  legible to the application."
  [filepath]
  (let [string-list (str/trim (slurp filepath)) ; TODO: error handling
        lines-list (str/split-lines string-list)
        goalkeepers-list (get-set-list lines-list :goalkeepers)
        players-list (get-set-list lines-list :players)
        substitutes-list (get-set-list lines-list :substitutes)
        guests-list (get-set-list lines-list :guests)]
    {:goalkeepers goalkeepers-list
     :players players-list
     :substitutes substitutes-list
     :guests guests-list}))

; Controller Functions
(defn to-pelada
  "Abstracts the parser to pelada model"
  [filepath]
  (parser filepath))

(defn generate-weekly-list
  "Returns the weekly list with the updated date"
  []
  (model/weekly-list))

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