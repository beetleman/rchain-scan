(ns rchain-scan.routes.uptime
  (:require [immutant.web.sse :as sse]
            [rchain-scan.uptime :refer [get-uptime]]
            [ring.util.http-response :refer :all]))

(defmulti uptime #(get-in % [:headers "accept"]))

(defmethod uptime :default [_]
  (ok (get-uptime)))

(defmethod uptime "text/event-stream" [request]
  (sse/as-channel request
                  {:on-open (fn [ch]
                              (while true
                                (sse/send! ch (assoc (get-uptime) :data true :type "uptime"))
                                (Thread/sleep 1000)))}))


(defn routes []
  {:get #(uptime %)})
