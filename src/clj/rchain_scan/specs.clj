(ns rchain-scan.specs
  (:require [spec-tools.core :as st]
            mount.core
            clojure.core.async.impl.channels)
  (:import [clojure.core.async.impl.channels ManyToManyChannel]
           [mount.core DerefableState]))


(def channel-spec (st/spec #(instance? ManyToManyChannel %)
                           {:reason "is not ManyToManyChannel instance"}))


(def derefable-state-spec (st/spec #(instance? DerefableState %)
                                   {:reason "is not DerefableState instance"}))
