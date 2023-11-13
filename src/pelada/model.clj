(ns pelada.model
  (:require [clojure.core.match :as matcher])
  (:require [clojure.string :as str]))

(defn- days-to-next-day
  "This functions calculates the day to next pelada day based on the current day"
  [day today]
  (let [next-day-constant (+ 7 day)]
    (if (= today day)
      0
      (if (< today day)
        (- day today)
        (- next-day-constant today)))))

(defn- parse-day
  "Parse the string day to number representation: 1 to 7"
  [day]
  (matcher/match [(str/lower-case day)]
                 ["segunda"] 1
                 ["terça"] 2
                 ["quarta"] 3
                 ["quinta"] 4
                 ["sexta"] 5
                 ["sabado"] 6
                 ["domingo"] 7)); this is unsafe because I'm not validating the strings

(defn- get-next-day
 "Retrieve the formatted date of the next pelada day" 
  [day]
  (let [now (java.time.LocalDate/now (java.time.ZoneId/of "GMT-3"))
        current-day-of-week (.getValue (.getDayOfWeek now))
        ; Math/max ensure that the number of days is positive
        next-day (.plusDays now (Math/max (days-to-next-day (parse-day day) current-day-of-week) 0))
        formatter (java.time.format.DateTimeFormatter/ofPattern "dd/MM")]
    (.format next-day formatter)))

(defn- list-string
  "Format the string with the given values"
  [date day hour owner place]
   (format 
     "*Lista Pelada %s %s %s, %s*\n\n*Goleiros*\n1.\n2.\n\n*Jogadores*\n1. %s\n\n*Suplentes*\n1.\n\n*Convidados*\n1.\n\n*Sub 15*\n1." 
     day date place hour owner))

(defn weekly-list
  "Generate the weekly list for the pelada"
  [read-setup-func]
  (let [setup (read-setup-func)
        owner (:owner setup)
        hour  (:hour setup)
        day   (:day setup)
        place (:place setup)]
    (list-string (get-next-day day) day hour owner place)))

(defn- team-maker-random
  "THis functions is responbile to choice and remove the player
  from the original list. it is tail recursive and returns a tuple with
  the team select and the updated players list"
  [acc players]
  (if (empty? players)
    [acc players]
    (let [chosen-player (rand-nth players)
          team (cons chosen-player acc)
          updated-players (remove #(= chosen-player %) players)]
      (if (= (count team) 5)
        [team updated-players]
        (team-maker-random team updated-players)))))

(defn team-maker
  "This function generate the 3 teams and returns it in tuple form."
  [pelada]
  (let [[first-team players-list-1] (team-maker-random '() (:players pelada))  
        [second-team players-list-2] (team-maker-random '() players-list-1)
        [third-team _] (team-maker-random '() players-list-2)]
    [first-team second-team third-team]))

(defn- print-team 
  "Format the team players to print"
  [team]
  (apply str (interpose "\n" team)))

(defn print-teams
  "Pretty print to show the teams"
  [[t1 t2 t3]]
  (println (format "Time 1: Vermelho\n%s" (print-team t1)))
  (println)
  (println (format "Time 2: Branco\n%s" (print-team t2)))
  (println)
  (println (format "Time 3: Preto\n%s" (print-team t3))))

(defn concierge-format
  "Prints a list formatted to send to the concierge"
  [pelada]
  (let [goalkeepers (:goalkeepers pelada)
        players (:players pelada)
        guests (:guests pelada)
        kids (:kids pelada)
        full-list (vec (concat goalkeepers players guests kids))]
    (doseq [player full-list]
      (println player))))
