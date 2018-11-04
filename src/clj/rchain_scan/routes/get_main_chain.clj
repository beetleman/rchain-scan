(ns rchain-scan.routes.get-main-chain
  (:require [rchain-scan.rnode :as rnode]
            [rchain-grpc.core :as grpc]
            [rchain-grpc.specs :refer [blocks-info-spec]]
            [spec-tools.spec :as spec]
            [ring.util.http-response :refer :all]
            [clojure.spec.alpha :as s]))

(defn get-main-chain [req]
  (let [depth (get-in req [:parameters :query :depth] 10)
        client (grpc/create-deploy-blocking-client rnode/channel)]
    (ok {:blocks (grpc/get-main-chain client depth)})))

(s/def ::depth spec/int?)
(s/def ::blocks blocks-info-spec)
(defn routes []
  {:get {:handler #(get-main-chain %)
         :responses {200 {:body (s/keys :req-un [::blocks])}}
         :parameters {:query (s/keys :opt-un [::depth])}}})
