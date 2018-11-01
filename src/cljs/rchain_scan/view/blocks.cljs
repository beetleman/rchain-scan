(ns rchain-scan.view.blocks
  (:require [re-frame.core :as rf]
            [baking-soda.core :as b]))


(defn block-hash
  ([x]
   (block-hash x 12))
  ([x size]
   (let [[text color] (if (empty? x)
                        ["None" "warning"]
                        [(str (.substring x 0 size) "...") "info"])]
     [b/Badge {:color color
               :pill  true}
      [:pre {:style {:margin    0
                     :font-size "1.2em"
                     :display   "inline"}}
       text]])))


(defn block-record [block-data name selector]
  [:div
   [:div.d-inline-block.text-truncate {:style {:width "150px"}}
    [:b name ": "]]
   [:div.d-inline-block.text-truncate {:style {:width         "calc(100% - 150px)"
                                               :white-space   "nowrap"
                                               :overflow      "hidden"
                                               :text-overflow "ellipsis"}}
    (selector block-data)]])


(defn block [block-data]
  [b/Card {:class-name "mb-5"}
   [b/CardHeader "Block Hash " [block-hash (:block-hash block-data)]]
   [b/CardBody
    [:div
     (map (fn [[name selector]]
            ^{:key selector} [block-record block-data name selector])
          [["Sender" :sender]
           ["Size" :block-size]
           ["Number" :block-number]
           ["Version" :version]
           ["Deploy count" :deploy-conut]
           ["Tuple spece hash" :tuple-space-hash]
           ["Time stamp" :timestamp]
           ["Faul tolerance" :faul-tolerance]])]]
   [b/CardFooter "Parent Hash " [block-hash (:main-parent-hash block-data)]]])


(defn blocks [blocks-data]
  [:div.col-md-12  {:style {:max-width 800}}
   (map (fn [block-data]
          ^{:key (:block-hash block-data)} [block block-data])
        blocks-data)])


(defn page []
  [b/Container
   [:div.d-flex.justify-content-center
    [blocks @(rf/subscribe [:blocks])]]])
