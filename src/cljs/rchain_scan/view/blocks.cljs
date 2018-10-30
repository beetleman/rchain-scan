(ns rchain-scan.view.blocks
  (:require [re-frame.core :as rf]))


(defn block-hash [x]
  [:pre (.substring x 0 10) "..."])


(defn blocks [blocks-hash]
  [:div
   (map (fn [hash]
          ^{:key hash} [block-hash hash])
         blocks-hash)])


(defn page []
  [:div "blocks"
   [blocks @(rf/subscribe [:blocks/hash])]])
