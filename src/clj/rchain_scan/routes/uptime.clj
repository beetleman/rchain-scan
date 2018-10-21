(ns rchain-scan.routes.uptime
  (:require [immutant.web.sse :as sse]
            [rchain-scan.uptime :refer [get-uptime]]
            [ring.util.http-response :refer :all]
            [clojure.core.async :as a :refer [<!] ]))

(defmulti uptime #(get-in % [:headers "accept"]))

(defmethod uptime :default [_]
  (ok {:data (get-uptime)}))

(defn- uptime-sse-handlers []
  (let [poison-ch (a/chan)]
    {:on-open  (fn [ch]
                 (a/go-loop [[_ c] [nil nil]]
                   (when-not (= c poison-ch)
                     (sse/send! ch {:data (get-uptime) :type "uptime"})
                     (recur (a/alts! [(a/timeout 1000) poison-ch])))))
     :on-close (fn [_ _]
                 (a/put! poison-ch :stop))}))

(defmethod uptime "text/event-stream" [request]
  (sse/as-channel request (uptime-sse-handlers)))

(defn routes []
  {:get #(uptime %)})
