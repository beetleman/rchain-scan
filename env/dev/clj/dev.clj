(ns dev
  (:require [rchain-scan.config :refer [env]]
            [clojure.spec.alpha :as s]
            [expound.alpha :as expound]
            [mount.core :as mount]
            [rchain-scan.figwheel :refer [start-fw stop-fw cljs]]
            [rchain-scan.core :refer [start-app]]
            [rchain-scan.db.core]
            [rchain-grpc.core] ;; for testing if it compile, remove after integraton
            [conman.core :as conman]
            [orchestra.spec.test :as st]
            [luminus-migrations.core :as migrations]))


(alter-var-root #'s/*explain-out* (constantly expound/printer))


(defn start []
  (st/instrument)
  (mount/start-without #'rchain-scan.core/repl-server))

(defn stop []
  (st/unstrument)
  (mount/stop-except #'rchain-scan.core/repl-server))

(defn restart []
  (stop)
  (start))

(defn restart-db []
  (mount/stop #'rchain-scan.db.core/*db*)
  (mount/start #'rchain-scan.db.core/*db*)
  (binding [*ns* 'rchain-scan.db.core]
    (conman/bind-connection rchain-scan.db.core/*db* "sql/queries.sql")))

(defn reset-db []
  (migrations/migrate ["reset"] (select-keys env [:database-url])))

(defn migrate []
  (migrations/migrate ["migrate"] (select-keys env [:database-url])))

(defn rollback []
  (migrations/migrate ["rollback"] (select-keys env [:database-url])))

(defn create-migration [name]
  (migrations/create name (select-keys env [:database-url])))
