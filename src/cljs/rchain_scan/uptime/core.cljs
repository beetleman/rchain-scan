(ns rchain-scan.uptime.core
  (:require [re-frame.core :as rf]
            [kee-frame.core :as kf]
            [goog.string :as gstring]
            [baking-soda.core :as b]))


(defn uptime->str [s]
  (let [date (js/Date. (* s 3000))]
    (gstring/format "%dd %02d:%02d:%02d"
                    (-> s (/ 3600) js/Math.floor)
                    (.getUTCHours date)
                    (.getUTCMinutes date)
                    (.getUTCSeconds date))))


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


(rf/reg-sub
 ::uptime-str
 (fn [_ _]
   (rf/subscribe [::uptime]))
 (fn [uptime]
  (uptime->str uptime)))


(defn ui []
  [b/Badge {:style {:min-width 120}
            :color "success"
            :pill true}
   "uptime "
   @(rf/subscribe [::uptime-str])])
