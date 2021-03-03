(ns da
  (:require
   [clojure.edn :as edn]
   [datalevin.core :as d]))

(def kanji (edn/read-string (slurp "/home/david/tmp/datalog-kanji/kanji.edn")))

(def schema {:kanji {:db/valueType :db.type/keyword}
             :freq {:db/cardinality :db.cardinality/one
                    :db/valueType :db.type/long}
             :readings_on {:db/cardinality :db.cardinality/many
                           :db/valueType :db.type/string}})

(def conn (d/get-conn "/home/david/tmp/datalog-kanji/kanji.db" schema))

(d/transact! conn kanji)

(d/q '[:find ?kanji ; <-- what to actually return from the query, i.e., the "kanji" field
       :in $ ?reading ; <-- an alias that represents the query value to search on, i.e., "形声文字"
       :where
       [?e :readings_on ?reading] ; <-- search in readings_on using the "reading" alias and bind the result to "?e"
       [?e :kanji ?kanji]] ; then from "?e" bind the value of ":kanji" to "?kanji" for returning
     @conn
     "形声文字") ;; #{[:漢字]}

(d/q '[:find ?kanji
       :in $ ?reading ; "reading" in this case == "wibble"
       :where
       [?e :readings_on ?reading]
       [?e :kanji ?kanji]]
     @conn
     "wibble") ;; #{} ; nothing found!

(d/q '[:find ?kanji
       :in $ ?reading ;; "reading" in this case == "形字"
       :where
       [?e :readings_on ?reading]
       [?e :kanji ?kanji]]
     @conn
     "形字") ;; #{[:漢]}

(d/close conn)
