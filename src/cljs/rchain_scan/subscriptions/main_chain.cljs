(ns rchain-scan.subscriptions.main-chain
  (:require [re-frame.core :as rf]
            [goog.string :as gstring]))


(rf/reg-sub
 :main-chain
 (fn [db _]
   (:main-chain db)))


(rf/reg-sub
 :main-chain/hash
 (fn [_ _]
   (rf/subscribe [:main-chain]))
 (fn [main-chain]
   (map :block-hash main-chain)))
