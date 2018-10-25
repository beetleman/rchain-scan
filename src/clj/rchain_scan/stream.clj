(ns rchain-scan.stream
  (:require [clojure.spec.alpha :as s]
            [spec-tools.spec :as spec]
            [rchain-scan.spec :refer [channel-spec]]
            [clojure.core.async :as a :refer [>! <!]]))


(defprotocol IStopable
  (stop [this]))

(defprotocol ISubscribable
  (subscribe [this] [this buffer])
  (unsubscribe [this sub]))

(defrecord StreamProvider [in-ch out-ch poison-ch]
  IStopable
  (stop [_] (a/put! poison-ch :stop))


  ISubscribable
  (subscribe [this]
    (subscribe this (a/sliding-buffer 100)))

  (subscribe [_ buffer]
    (let [sub (a/chan buffer)]
      (a/tap out-ch sub)
      sub))

  (unsubscribe [_ sub]
    (a/untap out-ch sub)
    (a/close! sub)))


(s/fdef create-interval-stream
  :args (s/cat :producer-fn channel-spec
               :ret spec/pos-int?)
  :ret #(instance? StreamProvider %))
(defn create-interval-stream [producer-fn interval]
  (let [in-ch     (a/chan (a/sliding-buffer 1))
        out-ch    (a/mult in-ch)
        poison-ch (a/chan 1)]
    (a/go-loop [[_ c] [nil nil]]
      (when-not (= c poison-ch)
        (>! in-ch (producer-fn))
        (recur (a/alts! [(a/timeout interval) poison-ch]))))
    (->StreamProvider in-ch out-ch poison-ch)))


(s/fdef on-msg
  :args (s/cat :sub channel-spec
               :handler fn?)
  :ret channel-spec)
(defn on-msg [sub handler]
    (a/go-loop []
      (when-let [data (<! sub)]
        (handler data)
        (recur))))
