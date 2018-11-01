(ns rchain-grpc.rho-types
  (:import [io.grpc.stub ClientCalls$BlockingResponseStream]
           [coop.rchain.casper.protocol CasperMessage$BlockInfoWithoutTuplespace
            CasperMessage$BlockQueryResponse
            CasperMessage$DeployServiceResponse
            CasperMessage$BlockInfo]))


(defprotocol RhoType
  (rho->clj [this]
    "convert any type returnet by grpc to clojure maps or records"))


(extend-protocol RhoType
  ClientCalls$BlockingResponseStream
  (rho->clj [responses]
    (map rho->clj (iterator-seq responses)))

  CasperMessage$BlockInfoWithoutTuplespace
  (rho->clj [info]
    {:block-hash       (.getBlockHash info)
     :block-size       (.getBlockSize info)
     :block-number     (.getBlockNumber info)
     :version          (.getVersion info)
     :deploy-conut     (.getDeployCount info)
     :tuple-space-hash (.getTupleSpaceHash info)
     :timestamp        (.getTimestamp info)
     :faul-tolerance   (.getFaultTolerance info)
     :main-parent-hash (.getMainParentHash info)
     :parent-hash-list (vec (.getParentsHashListList info))
     :sender           (.getSender info)})

  CasperMessage$BlockQueryResponse
   (rho->clj [response]
     {:status     (.getStatus response)
      :block-info (rho->clj (.getBlockInfo response))})

   CasperMessage$DeployServiceResponse
   (rho->clj [response]
     {:success (.getSuccess response)
      :message (.getMessage response)})

  CasperMessage$BlockInfo
   (rho->clj [info]
     {:block-hash       (.getBlockHash info)
      :block-size       (.getBlockSize info)
      :block-number     (.getBlockNumber info)
      :version          (.getVersion info)
      :deploy-conut     (.getDeployCount info)
      :tuple-space-hash (.getTupleSpaceHash info)
      :timestamp        (.getTimestamp info)
      :faul-tolerance   (.getFaultTolerance info)
      :main-parent-hash (.getMainParentHash info)
      :parent-hash-list (vec (.getParentsHashListList info))
      :sender           (.getSender info)
      :shard-id         (.getShardId info)
      :tuple-space-dump (.getTupleSpaceDump info)}))
