(ns rchain-grpc.core
  (:require [rchain-grpc.rho-types :refer [rho->clj]])
  (:import [coop.rchain.casper.protocol DeployServiceGrpc CasperMessage CasperMessage$BlocksQuery]
           [io.grpc ManagedChannelBuilder]))


(defn create-channel [host port]
  (-> (ManagedChannelBuilder/forAddress host port)
      (.usePlaintext true)
      .build))


(defn create-depoly-blocking-client [channel]
  (DeployServiceGrpc/newBlockingStub channel))


(defn blocks-query [depth]
  (-> (CasperMessage$BlocksQuery/newBuilder)
      (.setDepth depth)
      .build))


(defn get-blocks [client depth]
  (rho->clj (.showBlocks client
                         (blocks-query depth))))


(defn get-main-chain [client depth]
  (rho->clj (.showMainChain client
                            (blocks-query depth))))
