(ns rchain-scan.test.sse-handler
  (:require [clojure.test :refer :all]
            [rchain-scan.stream :as stream]
            [ring.mock.request :as ring-mock]
            [clojure.core.async :as a :refer [>! <!]]
            [immutant.web.sse :as sse]
            [rchain-scan.sse-handler :as handler]))

(deftest handler
  (a/<!!
   (a/go
     (testing "trying to set this shit up"
       (let [in-ch (a/chan (a/sliding-buffer 1))
             out-ch (a/mult in-ch)
             poison-ch (a/chan 1)
             test-stream (stream/->StreamProvider in-ch out-ch poison-ch)
             request (ring-mock/request :get "/testing")
             handler (handler/handler test-stream "test")
             request-channel (sse/as-channel request handler)]
         (>! in-ch "this is a test msg")
         (is (= "hello" (<! request-channel))))))))
