(ns rchain-scan.routes.uptime
  (:require [clojure.spec.alpha :as s]
            [rchain-scan.specs :refer [channel-spec]]
            [rchain-scan.stream :as stream]
            [rchain-scan.uptime :refer [get-uptime uptime-stream]]
            [ring.util.http-response :refer :all]
            [spec-tools.data-spec :as ds]
            [spec-tools.spec :as spec]
            [rchain-scan.sse :as sse]))


(defn uptime [_]
  (ok {:data (get-uptime)}))


(defn uptime-sse []
  (sse/create-sse-stream uptime-stream "uptime"))


(s/def ::data spec/nat-int?)
(defn routes []
  {:get {:handler   uptime
         :responses {200 {:body (s/keys :req-un [::data])}}}})


(defn streams-routes []
  {:get (uptime-sse)})
