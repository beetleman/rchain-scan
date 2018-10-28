(ns rchain-scan.test.uptime
  (:require [clojure.test :refer :all]
            [clj-time.core :as t]
            [clj-time.coerce :as tc]
            [clj-time.format :as tf]
            [fixtures :as fixtures]
            [rchain-scan.stream :as stream]
            [rchain-scan.uptime :as uptime]))


(use-fixtures
  :once
  fixtures/start-datetime)


(deftest calc-uptime
  (testing "uptime between 0 and 1000 epoch is 1000"
    (is (= 1000 (uptime/calc-uptime (tc/from-epoch 0)
                                    (tc/from-epoch 1000))))))


(deftest get-uptime
  (testing "value hange 1 after 1000ms"
    (let [uptime-1 (uptime/get-uptime)]
      (Thread/sleep 1000)
      (is (= 1
             (- (uptime/get-uptime) uptime-1))))))


(deftest create-uptime-stream
  (testing "return StreamProvider instance"
    (let [s (uptime/create-uptime-stream)]
      (is (stream/stream-provider? s)))))
