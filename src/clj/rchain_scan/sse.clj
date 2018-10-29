(ns rchain-scan.sse
  (:require [clojure.spec.alpha :as s]
            [immutant.web.sse :as sse]
            [rchain-scan.specs :refer [channel-spec derefable-state-spec]]
            [rchain-scan.stream :as stream]
            [rchain-scan.uptime :refer [uptime-stream]]
            [ring.util.http-response :refer :all]
            [spec-tools.data-spec :as ds]
            [spec-tools.spec :as spec]))


(def see-handler-spec (ds/spec {:name ::sse-handler
                                :spec {:on-open  fn?
                                       :on-close fn?}}))


(s/fdef sse-handler
  :args (s/cat :stream stream/stream-provider?
               :type spec/string?
               :transformer fn?)
  :ret see-handler-spec)
(defn- sse-handler
  ([stream type transformer]
   (let [sub (stream/subscribe stream)]
     {:on-open  (fn [ch]
                  (stream/on-msg sub
                                 (fn [data]
                                   (sse/send! ch {:data (transformer data)
                                                  :type type}))))
      :on-close (fn [_ _]
                  (stream/unsubscribe uptime-stream sub))})))



(s/fdef create-sse-stream
  :args (s/alt :resolved (s/cat :stream stream/stream-provider?
                                :type (s/? spec/string?)
                                :transformer (s/? fn?))
               :derefered (s/cat :stream derefable-state-spec
                                 :type (s/? spec/string?)
                                 :transformer (s/? fn?)))
  :ret fn?)
(defn create-sse-stream
  ([stream]
   (create-sse-stream stream "update"))
  ([stream type]
   (create-sse-stream stream type identity))
  ([stream type transformer]
   (fn [request]
     (let [accept (get-in request [:headers "accept"])]
       (if (= "text/event-stream" accept)
         (sse/as-channel request (sse-handler stream
                                              type
                                              transformer))
         (bad-request {:data "`text/event-stream` only!"}))))))
