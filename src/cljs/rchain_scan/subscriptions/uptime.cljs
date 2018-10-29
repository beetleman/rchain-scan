(ns rchain-scan.subscriptions.uptime
   (:require [re-frame.core :as rf]
             [goog.string :as gstring]))


(def hour-in-sec (* 60 60))
(def day-in-sec (* hour-in-sec 24))


(defn uptime->str [s]
  (let [date  (js/Date. (* s 1000))
        days  (/ s day-in-sec)
        hours (-> s
                  (mod day-in-sec)
                  (/ hour-in-sec))]
    (gstring/format "%dd %02d:%02d:%02d"
                    days
                    hours
                    (.getUTCMinutes date)
                    (.getUTCSeconds date))))


(rf/reg-sub
 :uptime
 (fn [db _]
   (:uptime db)))


(rf/reg-sub
 :uptime/str
 (fn [_ _]
   (rf/subscribe [:uptime]))
 (fn [uptime]
  (uptime->str uptime)))
