(ns fixtures
  (:require [rchain-scan.db.core]
            [rchain-scan.rnode]
            [luminus-migrations.core :as migrations]
            [clojure.java.jdbc :as jdbc]
            [rchain-scan.config :as config]
            [mount.core :as mount]))


(defn env [f]
  (mount/start #'rchain-scan.config/env)
  (f))


(defn db [f]
  (env (fn []
         (mount/start #'rchain-scan.db.core/*db*)
         (migrations/migrate ["migrate"] (select-keys config/env [:database-url]))
         (f))))

(defn channel [f]
  (env (fn []
         (mount/start #'rchain-scan.rnode/channel)
         (f))))

(defn app [f]
  (env (fn []
         (mount/start #'rchain-scan.handler/app)
         (f))))
