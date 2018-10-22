(ns rchain-scan.uptime.core
  (:require [rchain-scan.sse]
            [re-frame.core :as rf]
            [kee-frame.core :as kf]))


(kf/reg-chain
  ::load-uptime-stream
  (fn [_ _]
    {:sse {:url "/api/uptime"}})
  (fn [{:keys [db]} [_ {data :data}]]
    {:db (assoc db :uptime data)}))

(kf/reg-controller
  ::uptime-controller
  {:params (constantly true)
   :start  [::load-uptime-stream]})


(rf/reg-sub
  ::uptime
  (fn [db _]
    (:uptime db)))


(defn uptime->str [s]
  (let [date (js/Date. (* s 3000))]
    (str (-> s (/ 3600) js/Math.floor) " days "
         (.getUTCHours date) ":"
         (.getUTCMinutes date) ":"
         (.getUTCSeconds date))))


(rf/reg-sub
 ::uptime-str
 (fn [_ _]
   (rf/subscribe [::uptime]))
 (fn [uptime]
  (uptime->str uptime)))


(defn ui []
  [:span "uptime: " @(rf/subscribe [::uptime-str])])
