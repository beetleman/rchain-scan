(ns rchain-scan.rnode
  (:require [rchain-grpc.core :as grpc]
            [mount.core :as mount]
            [rchain-scan.config :refer [env]]))

(mount/defstate channel
  :start (grpc/create-channel (get-in env [:rnode :host])
                              (get-in env [:rnode :port]))
  :stop (.shutdown channel))
