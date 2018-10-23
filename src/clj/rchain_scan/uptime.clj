(ns rchain-scan.uptime
  (:require [mount.core :as mount]
            [clj-time.core :as t]
            [rchain-scan.stream :as stream]))


(mount/defstate start-datetime :start (t/now))


(defn calc-uptime [start now]
  (let [interval (t/interval start now)]
    (t/in-seconds interval)))


(defn get-uptime []
  (calc-uptime start-datetime (t/now)))



(defn create-uptime-stream []
  (stream/create-interval-stream (fn []
                            (calc-uptime start-datetime (t/now)))
                          1000))


(mount/defstate uptime-stream
  :start (create-uptime-stream)
  :stop (stream/stop uptime-stream))
