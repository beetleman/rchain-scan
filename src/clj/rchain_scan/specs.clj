(ns rchain-scan.specs
  (:require [spec-tools.core :as st]
            clojure.core.async.impl.channels)
  (:import [clojure.core.async.impl.channels ManyToManyChannel]))


(defn channel-spec [i]
  (st/spec #(instance?  %)
           {:reason "is not ManyToManyChannel instance"}))
