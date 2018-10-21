(ns rchain-scan.routes.get-blocks
  (:require [rchain-scan.rnode :as rnode]
            [rchain-grpc.core :as grpc]
            [rchain-grpc.spec :refer [blocks-info-spec]]
            [spec-tools.spec :as spec]
            [ring.util.http-response :refer :all]
            [clojure.spec.alpha :as s]))

(defn get-blocks [req]
  (let [depth (get-in req [:parameters :query :depth] 10)
        client (grpc/create-deploy-blocking-client rnode/channel)]
    (ok {:blocks (grpc/get-blocks client depth)})))

(s/def ::depth spec/int?)
(s/def ::blocks blocks-info-spec)
(defn routes []
  {:get {:handler #(get-blocks %)
         :coercion reitit.coercion.spec/coercion
         :responses {200 {:body (s/keys :req-un [::blocks])}}
         :parameters {:query (s/keys :opt-un [::depth])}}})
