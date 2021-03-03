(ns da
  (:require
   [clojure.edn :as edn]
   [datalevin.core :as d]))

(def kanji (edn/read-string (slurp "/home/david/tmp/datalog-kanji/kanji.edn")))

(def schema {:kanji {:db/cardinality :db.cardinality/many
                     :db/valueType :db.type/keyword}
             :freq {:db/cardinality :db.cardinality/one
                    :db/valueType :db.type/long}
             :readings_on {:db/cardinality :db.cardinality/many
                           :db/valueType :db.type/string}})

(def conn (d/get-conn "/home/david/tmp/datalog-kanji/kanjidb" schema))

(d/transact! conn [kanji])

(d/q '[:find (d/pull ?e [*])
       :in $ ?readings_on
       :where
       [?e :kanji ?kanji]
       [?e :readings_on ?readings_on]]
     @conn
     "形声文字") ;; ([{:db/id 1, :kanji [:漢字], :freq 1, :readings_on ["形声文字"]}])

(d/q '[:find (d/pull ?e [*])
       :in $ ?readings_on
       :where
       [?e :kanji ?kanji]
       [?e :readings_on ?readings_on]]
     @conn
     "wibble") ;; ()

(d/close conn)
