(ns rchain-scan.subscriptions.blocks
  (:require [re-frame.core :as rf]
            [goog.string :as gstring]))


(rf/reg-sub
 :blocks
 (fn [db _]
   (:blocks db)))


(rf/reg-sub
 :blocks/hash
 (fn [_ _]
   (rf/subscribe [:blocks]))
 (fn [blocks]
   (map :block-hash blocks)))
