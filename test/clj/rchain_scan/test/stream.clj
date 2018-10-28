(ns rchain-scan.test.stream
  (:require [clojure.test :refer :all]
            [clojure.core.async :as a :refer [>! <!]]
            [clj-time.core :as t]
            [clj-time.coerce :as tc]
            [rchain-scan.stream :as stream]))

(defn epoch []
  (tc/to-epoch (t/now)))

(deftest create-internal-stream
  (a/<!! (a/go
           (let [start-epoch   (epoch)
                 only-1-stream (stream/create-interval-stream (constantly 1) 10)
                 epoch-stream  (stream/create-interval-stream epoch 1000)]

             (testing "sub is avialable from stream"
               (let [sub (stream/subscribe only-1-stream)]
                 (is (= 1 (<! sub)))
                 (is (= 1 (<! sub)))))

             (testing "check if respect interval"
               (let [sub (stream/subscribe epoch-stream)]
                 (is (= -1 (- (<! sub)
                              (<! sub))))))

             (testing "stream can be closed"
               (doseq [s [only-1-stream epoch-stream]]
                 (let [sub (stream/subscribe s)]
                   (stream/stop s)
                   (<! sub) ; it can be value in buffer so i ignore first result
                   (is (nil? (<! sub)))))))))) ;channels returns nil if are closed


(deftest on-msg
  (a/<!! (a/go
           (testing "call handler on every message and stop when sub closed"
             (let [messages (range 10)
                   sub (a/to-chan messages) ; chan closed after read of 10 messages
                   recived-messages (atom [])]
               (<! (stream/on-msg sub
                                  (fn [m]
                                    (swap! recived-messages conj m))))
               (is (= messages @recived-messages)))))))
