(ns rchain-scan.view.uptime
  (:require [re-frame.core :as rf]
            [baking-soda.core :as b]))


(defn badge []
  [b/Badge {:style {:min-width 120}
            :color "success"
            :pill true}
   "uptime "
   @(rf/subscribe [:uptime/str])])
