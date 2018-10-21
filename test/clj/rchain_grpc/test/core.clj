(ns rchain-grpc.test.core
  (:require [rchain-grpc.core :as core]
            [rchain-scan.config :refer [env]]
            [mount.core :as mount]
            [fixtures :as fixtures]
            [clojure.test :refer :all]
            [orchestra.spec.test :as st])
  (:import [io.grpc Channel]
           [io.grpc.stub AbstractStub]
           [coop.rchain.casper.protocol CasperMessage$BlocksQuery CasperMessage$BlockQuery]))

(st/instrument)

(use-fixtures
  :once
  fixtures/env)


(defn get-channel []
  (core/create-channel (get-in env [:rnode :host])
                       (get-in env [:rnode :port])))



(deftest create-channel
  (let [channel (get-channel)]
    (is (instance? Channel
                   channel))
    (.shutdown channel)))


(deftest create-depoly-blocking-client
  (let [channel (get-channel)
        client (core/create-depoly-blocking-client channel)]
    (is (instance? AbstractStub client))
    (.shutdown channel)))


(deftest blocks-query
  (is (instance? CasperMessage$BlocksQuery (#'core/blocks-query 1))))


(deftest blocks-query
  (is (instance? CasperMessage$BlockQuery
                 (#'core/block-query "4ce488c86f2276cff6917daeee5365553060d2ebaf5878b9eec6922bb22b9c2d"))))



(deftest get-blocks
  (let [channel (get-channel)
        client  (core/create-depoly-blocking-client channel)]
    (let [blocks (core/get-blocks client 1)]
      (-> blocks first )
      (is (= (-> blocks first keys set)
             #{:block-size
               :tuple-space-hash
               :block-hash
               :faul-tolerance
               :deploy-conut
               :block-number
               :main-parent-hash
               :sender
               :parent-hash-list
               :version
               :timestamp})))
    (.shutdown channel)))


(deftest get-main-chain
  (let [channel (get-channel)
        client  (core/create-depoly-blocking-client channel)]
    (let [blocks (core/get-main-chain client 1)]
      (is (= (-> blocks first keys set)
             #{:block-size
               :tuple-space-hash
               :block-hash
               :faul-tolerance
               :deploy-conut
               :block-number
               :main-parent-hash
               :sender
               :parent-hash-list
               :version
               :timestamp}))
      (-> blocks first :parent-hash-list class clojure.pprint/pprint))
    (.shutdown channel)))


(deftest get-block
  (let [channel         (get-channel)
        client          (core/create-depoly-blocking-client channel)
        block-from-list (first (core/get-blocks client 1))
        hash            (:block-hash block-from-list)]
    (is (= block-from-list
           (dissoc (:block-info (core/get-block client hash))
                   :shard-id :tuple-space-dump)))
    (.shutdown channel)))
