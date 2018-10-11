(ns rchain-scan.routes.home
  (:require [rchain-scan.layout :as layout]
            [rchain-scan.db.core :as db]
            [clojure.java.io :as io]
            [rchain-scan.middleware :as middleware]
            [ring.util.http-response :as response]))

(defn home-page [_]
  (layout/render "home.html"))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/docs" {:get (fn [_]
                    (-> (response/ok (-> "docs/docs.md" io/resource slurp))
                        (response/header "Content-Type" "text/plain; charset=utf-8")))}]])

