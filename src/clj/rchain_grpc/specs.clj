(ns rchain-grpc.specs
  (:require [spec-tools.data-spec :as ds]
            [spec-tools.spec :as spec]
            [clojure.spec.alpha :as s]
            [spec-tools.core :as st])
  (:import [io.grpc Channel]
           [io.grpc.stub AbstractStub]))


(def client-spec (st/spec #(instance? AbstractStub %)
                          {:reason "is not AbstractStub instance"}))


(def channel-spec (st/spec #(instance? Channel %)
                           {:reason "is not Channel instance"}))


(def block-info-without-tuplespace
  {:block-hash       spec/string?
   :block-size       spec/string?
   :block-number     spec/nat-int?
   :version          spec/nat-int?
   :deploy-conut     spec/pos-int?
   :tuple-space-hash spec/string?
   :timestamp        spec/nat-int?
   :faul-tolerance   spec/float?
   :main-parent-hash spec/string?
   :parent-hash-list [spec/string?]
   :sender           spec/string?})

(def block-info-without-tuplespace-spec (ds/spec {:name ::block-info-without-tuplespace
                                                  :spec block-info-without-tuplespace}))



(def block-info
  (merge block-info-without-tuplespace
         {:shard-id         spec/string?
          :tuple-space-dump spec/string?}))

(def block-info-spec (ds/spec ::block-info
                              block-info))


(def blocks-info [block-info-without-tuplespace])

(def blocks-info-spec (ds/spec {:name ::blocks-info
                                :spec blocks-info}))


(def block-query-response {:status     spec/string?
                           :block-info block-info})

(def block-query-response-spec (ds/spec {:name ::block-query-response
                                         :spec block-query-response}))
