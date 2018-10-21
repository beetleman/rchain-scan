(ns rchain-scan.test.routes.get-blocks
  (:require [rchain-scan.routes.get-blocks :as routes]
            [ring.mock.request :as ring-mock]
            [fixtures :as fixtures]
            [muuntaja.core :as m]
            [rchain-scan.middleware.formats :as formats]
            [rchain-scan.handler :refer :all]
            [clojure.test :refer :all]))

(use-fixtures
  :once
  fixtures/channel
  fixtures/app)

(defn parse-json [body]
  (m/decode formats/instance "application/json" body))

(defn get-body [resp] (-> resp :body parse-json))

(deftest get-blocks
  (testing "GET"
    (testing "default response"
      (let [resp (app (ring-mock/request :get "/api/blocks"))
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

