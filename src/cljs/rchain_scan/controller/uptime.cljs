(ns rchain-scan.controller.uptime
   (:require [re-frame.core :as rf]
             [kee-frame.core :as kf]))

(kf/reg-chain
  ::load-uptime-stream
  (fn [_ _]
    {:sse {:url "/api/stream/uptime"}})
  (fn [{:keys [db]} [_ {data :data}]]
    {:db (assoc db :uptime data)}))


(kf/reg-controller
  ::uptime-controller
  {:params (constantly true)
   :start  [::load-uptime-stream]})
