(ns rchain-scan.routes.uptime
  (:require [immutant.web.sse :as sse]
            [rchain-scan.uptime :refer [get-uptime uptime-stream]]
            [rchain-scan.stream :as stream]
            [ring.util.http-response :refer :all]
            [spec-tools.spec :as spec]
            [clojure.spec.alpha :as s]
            [clojure.core.async :as a :refer [<!]]))

(defmulti uptime #(get-in % [:headers "accept"]))

(defmethod uptime :default [_]
  (ok {:data (get-uptime)}))

(defn- uptime-sse-handlers [sub]
  {:on-open  (fn [ch]
               (stream/on-msg sub
                              (fn [data]
                                (sse/send! ch {:data data :type "uptime"}))))
   :on-close (fn [_ _]
               (stream/unsubscribe uptime-stream sub))})

(defmethod uptime "text/event-stream" [request]
  (sse/as-channel request (uptime-sse-handlers (stream/subscribe uptime-stream))))


(s/def ::data spec/nat-int?)
(defn routes []
  {:get {:handler   #(uptime %)
         :responses {200 {:body (s/keys :req-un [::data])}}}})
