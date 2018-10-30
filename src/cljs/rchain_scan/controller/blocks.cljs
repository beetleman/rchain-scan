(ns rchain-scan.controller.blocks
  (:require [re-frame.core :as rf]
            [kee-frame.core :as kf]))


(kf/reg-chain
  ::load-blocks
  (fn [_ _]
    {:http {:method :get
            :url    "/api/blocks"}})
  (fn [{:keys [db]} [_ {blocks :blocks}]]
    {:db (assoc db :blocks blocks)}))


(kf/reg-controller
  ::blocks-controller
  {:params (fn [{{name :name} :data}]
             (or (= name :blocks)
                 nil))
   :start  [::load-blocks]})
