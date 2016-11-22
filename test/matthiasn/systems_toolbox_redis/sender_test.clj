(ns matthiasn.systems-toolbox-redis.sender-test
  (:require [clojure.test :refer :all]
            [clojure.tools.logging :as log]
            [matthiasn.systems-toolbox-redis.sender :as s]))

(deftest cmp-map-test
  (testing "it warns if no relay-types are given"
    (let [logger (atom [])]
      (with-redefs [log/log* (fn [_ _ _ msg] (swap! logger conj msg))]
        (s/cmp-map :test/some-id {})
        (is (= ["using redis-cmp without specified :relay-types not recommended"]
               @logger))))))
