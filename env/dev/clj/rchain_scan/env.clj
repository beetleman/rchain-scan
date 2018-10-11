(ns rchain-scan.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [rchain-scan.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[rchain-scan started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[rchain-scan has shut down successfully]=-"))
   :middleware wrap-dev})
