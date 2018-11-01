(ns rchain-grpc.core
  (:require [clojure.spec.alpha :as s]
            [rchain-grpc.rho-types :refer [rho->clj]]
            [rchain-grpc.specs :refer [block-query-response-spec
                                       blocks-info-spec
                                       channel-spec
                                       deploy-service-response-spec
                                       client-spec]]
            [clj-time.core :as t]
            [clj-time.coerce :as c]
            [spec-tools.spec :as spec])
  (:import [coop.rchain.casper.protocol CasperMessage
            CasperMessage$BlockQuery
            CasperMessage$BlocksQuery
            CasperMessage$DeployData
            CasperMessage$PhloLimit
            CasperMessage$PhloPrice
            DeployServiceGrpc]
           io.grpc.ManagedChannelBuilder
           [com.google.protobuf Empty]))


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

(defn- phlo-limit [value]
  (-> (CasperMessage$PhloLimit/newBuilder)
      (.setValue value)
      .build))

(defn- phlo-price [value]
  (-> (CasperMessage$PhloPrice/newBuilder)
      (.setValue value)
      .build))

(defn- deploy-data [term from phlo-limit phlo-price nonce timestamp]
  (-> (CasperMessage$DeployData/newBuilder)
      (.setTerm term)
      (.setFrom from)
      (.setPhloLimit phlo-limit)
      (.setPhloPrice phlo-price)
      (.setNonce nonce)
      (.setTimestamp timestamp)
      .build))

(s/fdef deploy
  :args (s/cat :channel           client-spec
               :term              spec/string?
               :from              (s/? spec/string?)
               :phlo-limit-value  (s/? spec/pos-int?)
               :phlo-price-value  (s/? spec/pos-int?)
               :nonce             (s/? spec/nat-int?)
               :timestamp         (s/? spec/pos-int?))
  :ret deploy-service-response-spec)
(defn deploy
  ([client term]
   (let [timestamp (c/to-long (t/now))]
     (deploy client term "0x0" 10000000 1 0 timestamp)))

  ([client term from phlo-limit-value phlo-price-value nonce timestamp]
   (let [limit (phlo-limit phlo-limit-value)
         price (phlo-price phlo-price-value)]
     (rho->clj (.doDeploy client
                         (deploy-data term from limit price nonce timestamp))))))

(defn- empty []
  (-> (Empty/newBuilder)
      .build))

(s/fdef propose
  :args (s/cat :client client-spec)
  :ret deploy-service-response-spec)
(defn propose [client]
  (rho->clj (.createBlock client (empty))))
