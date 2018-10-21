(ns rchain-grpc.core
  (:require [clojure.spec.alpha :as s]
            [rchain-grpc.rho-types :refer [rho->clj]]
            [rchain-grpc.spec :refer [block-query-response-spec
                                      blocks-info-spec
                                      channel-spec
                                      client-spec]]
            [spec-tools.spec :as spec])
  (:import [coop.rchain.casper.protocol CasperMessage
                                        CasperMessage$BlockQuery
                                        CasperMessage$BlocksQuery
                                        DeployServiceGrpc]
           io.grpc.ManagedChannelBuilder))


(s/fdef create-channel
  :args (s/cat :host spec/string? :port spec/int?)
  :ret channel-spec)
(defn create-channel [host port]
  (-> (ManagedChannelBuilder/forAddress host port)
      (.usePlaintext true)
      .build))


(s/fdef create-deploy-blocking-client
  :args (s/cat :channel channel-spec)
  :ret client-spec)
(defn create-deploy-blocking-client [channel]
  (DeployServiceGrpc/newBlockingStub channel))


(defn- blocks-query [depth]
  (-> (CasperMessage$BlocksQuery/newBuilder)
      (.setDepth depth)
      .build))


(defn- block-query [hash]
  (-> (CasperMessage$BlockQuery/newBuilder)
      (.setHash hash)
      .build))


(s/fdef get-blocks
  :args (s/cat :client client-spec :depth spec/pos-int?)
  :ret blocks-info-spec)
(defn get-blocks [client depth]
  (rho->clj (.showBlocks client
                         (blocks-query depth))))


(s/fdef get-main-chain
  :args (s/cat :client client-spec :depth spec/pos-int?)
  :ret blocks-info-spec)
(defn get-main-chain [client depth]
  (rho->clj (.showMainChain client
                            (blocks-query depth))))


(s/fdef get-block
  :args (s/cat :client client-spec :depth spec/string?)
  :ret block-query-response-spec)
(defn get-block [client hash]
  (rho->clj (.showBlock client
                        (block-query hash))))
