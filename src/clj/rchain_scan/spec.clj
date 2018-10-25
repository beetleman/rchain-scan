(ns rchain-scan.spec
  (:require [spec-tools.spec :as spec])
  (:import [clojure.core.async.impl.channels ManyToManyChannel]))

(defn channel-spec [i]
  (st/spec #(instance?  %)
           {:reason "is not ManyToManyChannel instance"}))
