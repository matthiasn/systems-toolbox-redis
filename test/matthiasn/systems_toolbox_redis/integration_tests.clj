(ns matthiasn.systems-toolbox-redis.integration-tests
  (:require [clojure.test :refer :all]
            [matthiasn.systems-toolbox-redis.test-helper :refer [eventually]]
            [matthiasn.systems-toolbox.switchboard :as sb]
            [matthiasn.systems-toolbox-redis.receiver :as rr]
            [matthiasn.systems-toolbox-redis.sender :as rs]
            [clojure.spec :as s]))

(s/def :test/msg (s/map-of keyword? number?))

(deftest basic-test
  (let [sb (sb/component :test/switchboard)
        conf {:host        "localhost"
              :port        6379
              :topic       "test-topic"
              :relay-types #{:test/msg}}
        rcv-state (atom [])
        test-cmp-map {:cmp-id      :test/inbox
                      :state-fn    (fn [_put-fn] {:state rcv-state})
                      :handler-map {:test/msg (fn [{:keys [current-state msg msg-meta]}]
                                                {:new-state (conj current-state [msg msg-meta])})}}]
    (sb/send-mult-cmd sb
                      [[:cmd/init-comp
                        #{(rr/cmp-map :test/receiver conf)
                          (rs/cmp-map :test/sender conf)
                          test-cmp-map}]

                       [:cmd/route {:from :test/receiver
                                    :to   :test/inbox}]

                       [:cmd/send {:to  :test/sender
                                   :msg [:test/msg {:a 1 :b 2}]}]])

    (testing "it should send and receive message via Redis"
      (eventually (= 1
                     (count @rcv-state)))
      (eventually (= [:test/msg {:a 1 :b 2}]
                     (ffirst @rcv-state))))
    (testing "meta-data on message is preserved"
      (let [msg-meta (second (first @rcv-state))]
        (eventually (= #{:cmp-seq :corr-id :tag :test/inbox :test/receiver :test/sender}
                       (set (keys msg-meta))))
        (eventually (= [:test/sender :test/inbox]
                       (:cmp-seq msg-meta)))))))
