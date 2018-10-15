(ns rchain-scan.uptime
  (:require [mount.core :as mount]
            [clj-time.core :as t]))

(mount/defstate start-datetime :start (t/now))

(defn calc-uptime [start now]
  (let [interval (t/interval start now)]
    {:seconds (t/in-seconds interval)}))

(defn get-uptime []
  (calc-uptime start-datetime (t/now)))
