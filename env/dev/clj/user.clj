(ns user
  (:require [rchain-scan.config :refer [env]]
            [clojure.spec.alpha :as s]
            [expound.alpha :as expound]
            [mount.core :as mount]
            [rchain-scan.figwheel :refer [start-fw stop-fw cljs]]
            [conman.core :as conman]
            [orchestra.spec.test :as st]
            [luminus-migrations.core :as migrations]))


(alter-var-root #'s/*explain-out* (constantly expound/printer))

(defn start []
  (require 'rchain-scan.core)
  (st/instrument)
  (mount/start-without (ns-resolve 'rchain-scan.core 'repl-server)))

(defn stop []
  (require 'rchain-scan.core)
  (st/unstrument)
  (mount/stop-except (ns-resolve 'rchain-scan.core 'repl-server)))

(defn restart []
  (stop)
  (start))

(defn restart-db []
  (require 'rchain-scan.db.core)
  (mount/stop (ns-resolve 'rchain-scan.db.core '*db*))
  (mount/start (ns-resolve 'rchain-scan.db.core '*db*))
  (binding [*ns* 'rchain-scan.db.core]
    (conman/bind-connection (ns-resolve 'rchain-scan.db.core '*db*) "sql/queries.sql")))

(defn reset-db []
  (migrations/migrate ["reset"] (select-keys env [:database-url])))

(defn migrate []
  (migrations/migrate ["migrate"] (select-keys env [:database-url])))

(defn rollback []
  (migrations/migrate ["rollback"] (select-keys env [:database-url])))

(defn create-migration [name]
  (migrations/create name (select-keys env [:database-url])))
