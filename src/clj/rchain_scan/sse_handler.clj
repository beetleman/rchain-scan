(ns rchain-scan.sse-handler
  (:require [immutant.web.sse :as sse]
            [rchain-scan.stream :as stream]))


(defn handler [api-stream msg-type]
  (let [sub (stream/subscribe api-stream)]
    {:on-open  (fn [ch]
                 (stream/on-msg sub
                                (fn [data]
                                  (sse/send! ch {:data data :type msg-type}))))
     :on-close (fn [_ _]
                 (stream/unsubscribe api-stream sub))}))

