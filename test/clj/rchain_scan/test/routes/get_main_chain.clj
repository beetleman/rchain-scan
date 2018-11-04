(ns rchain-scan.test.routes.get-main-chain
  (:require [rchain-scan.routes.get-main-chain :as routes]
            [ring.mock.request :as ring-mock]
            [fixtures :as fixtures]
            [muuntaja.core :as m]
            [rchain-scan.middleware.formats :as formats]
            [rchain-scan.handler :refer :all]
            [rchain-grpc.core :as grpc]
            [rchain-scan.rnode :as rnode]
            [clojure.test :refer :all]))

(use-fixtures
  :once
  fixtures/channel
  fixtures/app)

(defn parse-json [body]
  (m/decode formats/instance "application/json" body))

(defn get-body [resp] (-> resp :body parse-json))

(deftest get-main-chain
  (let [client (grpc/create-deploy-blocking-client rnode/channel)]
    (fixtures/create-blocks-if-less client 10))

  (testing "GET"
    (testing "default response"
      (let [resp (app (ring-mock/request :get "/api/main-chain"))
            body (get-body resp)]
        (is (= 200 (:status resp)))
        (is (= 10 (-> body :blocks count)))
        (is (= (-> body :blocks first keys set)
               #{:block-size
                 :tuple-space-hash
                 :block-hash
                 :faul-tolerance
                 :deploy-conut
                 :block-number
                 :main-parent-hash
                 :sender
                 :parent-hash-list
                 :version
                 :timestamp}))))
    (testing "specifying depth"
      (let [depth 2
            resp (app (ring-mock/request :get (str "/api/blocks?depth=" depth "&test=a")))
            body (get-body resp)]
        (is (= depth (-> body :blocks count)))))))
