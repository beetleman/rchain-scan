(ns rchain-grpc.test.core
  (:require [rchain-grpc.core :as core]
            [rchain-scan.config :refer [env]]
            [mount.core :as mount]
            [fixtures :as fixtures]
            [clojure.test :refer :all])
  (:import [io.grpc Channel]))


(use-fixtures
  :once
  fixtures/env)


(deftest create-channel
  (let [channel (core/create-channel (get-in env [:rnode :host])
                                     (get-in env [:rnode :port]))]
    (is (instance? Channel
                   channel))
    (.shutdown channel)))
