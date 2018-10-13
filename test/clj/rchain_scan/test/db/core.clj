(ns rchain-scan.test.db.core
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.test :refer :all]
            [fixtures :as fixtures]
            [rchain-scan.db.core :as db :refer [*db*]]))

(use-fixtures
  :once
  fixtures/db)


(deftest test-users
  (jdbc/with-db-transaction [t-conn *db*]
    (jdbc/db-set-rollback-only! t-conn)
    (is (= 1 (db/create-user!
               t-conn
               {:id         "1"
                :first_name "Sam"
                :last_name  "Smith"
                :email      "sam.smith@example.com"
                :pass       "pass"})))
    (is (= {:id         "1"
            :first_name "Sam"
            :last_name  "Smith"
            :email      "sam.smith@example.com"
            :pass       "pass"
            :admin      nil
            :last_login nil
            :is_active  nil}
           (db/get-user t-conn {:id "1"})))))
