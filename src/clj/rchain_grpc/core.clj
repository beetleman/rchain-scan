(ns rchain-grpc.core
  (:import [coop.rchain.casper.protocol DeployServiceGrpc CasperMessage]
           [io.grpc ManagedChannelBuilder]))


(defn create-channel [host port]
  (-> (ManagedChannelBuilder/forAddress host port)
      (.usePlaintext true)
      .build))


(defn create-depoly-blockin-stub [channel]
  (DeployServiceGrpc/newBlockingStub channel))
