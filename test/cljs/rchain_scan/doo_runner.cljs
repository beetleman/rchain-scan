(ns rchain-scan.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [rchain-scan.core-test]))

(doo-tests 'rchain-scan.core-test)

