(ns fixtures
  (:require [rchain-scan.db.core]
            [rchain-scan.rnode]
            [rchain-scan.handler]
            [rchain-scan.uptime]
            [luminus-migrations.core :as migrations]
            [selmer.parser :refer [render-file render]]
            [clojure.java.jdbc :as jdbc]
            [rchain-scan.config :as config]
            [mount.core :as mount]
            [rchain-grpc.core :as grpc]))

(defn env [f]
  (mount/start #'rchain-scan.config/env)
  (f))


(defn start-datetime [f]
  (mount/start #'rchain-scan.uptime/start-datetime)
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
  (start-datetime
   #(env
     (fn []
       (mount/start #'rchain-scan.handler/app)
       (f)))))

(def contract-template "contract @\"add_{{number}}_{{scope}}\"(@number, cb) = {
    cb!(number + {{number}})
  }
  |
  new foo in {
    @\"add_{{number}}\"!(42, *foo)
  }
  ")

(defn create-blocks [client i]
  (dotimes [n i]
    (grpc/deploy client
                 (render contract-template {:number n :scope "tests"}))
    (grpc/propose client)))

(defn create-blocks-if-less [client i]
  (let [blocks-count (count (grpc/get-blocks client i))]
    (when (< blocks-count i)
      (create-blocks client (- i blocks-count)))))
