(ns rchain-scan.routes.services
  (:require [muuntaja.middleware :as muuntaja]
            [reitit.ring.coercion :as rrc]
            [reitit.swagger :as swagger]
            [reitit.coercion.spec]
            [ring.util.http-response :refer :all]
            [ring.middleware.params :as params]
            [rchain-scan.routes.get-blocks :as get-blocks]
            [rchain-scan.routes.uptime :as uptime]))

(defn service-routes []
  ["/api"
   {:middleware [params/wrap-params
                 muuntaja/wrap-format
                 swagger/swagger-feature
                 rrc/coerce-exceptions-middleware
                 rrc/coerce-request-middleware
                 rrc/coerce-response-middleware]
    :swagger {:id ::api
              :info {:title "my-api"
                     :description "using [reitit](https://github.com/metosin/reitit)."}
              :produces #{"application/json"
                          "application/edn"
                          "application/transit+json"}
              :consumes #{"application/json"
                          "application/edn"
                          "application/transit+json"}}}
   ["/swagger.json"
    {:get {:no-doc true
           :handler (swagger/create-swagger-handler)}}]
   ["/ping" {:get (constantly (ok {:message "ping"}))}]
   ["/pong" {:post (constantly (ok {:message "pong"}))}]
   ["/blocks" (get-blocks/routes)]
   ["/uptime" (uptime/routes)]])
