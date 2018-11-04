(ns rchain-scan.controller.main-chain
  (:require [re-frame.core :as rf]
            [kee-frame.core :as kf]))


(kf/reg-chain
  ::load-main-chain
  (fn [_ _]
    {:http {:method :get
            :url    "/api/main-chain"}})
  (fn [{:keys [db]} [_ {main-chain :blocks}]]
    {:db (assoc db :main-chain main-chain)}))


(kf/reg-controller
  ::main-chain-controller
  {:params (fn [{{name :name} :data}]
             (or (= name :main-chain)
                 nil))
   :start  [::load-main-chain]})
