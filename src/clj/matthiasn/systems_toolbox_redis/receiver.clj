(ns matthiasn.systems-toolbox-redis.receiver
  (:require [matthiasn.systems-toolbox-redis.spec]
            [taoensso.carmine :as car]
            [clojure.tools.logging :as log]))

(defn- msg-handler-fn
  "Create handler function for messages from Redis Pub/Sub"
  [put-fn]
  (fn [[msg-type _topic payload]]
    (when (= msg-type "message")
      (let [[cmd-type {:keys [msg msg-meta]}] payload]
        (put-fn (with-meta [cmd-type msg] msg-meta))))))

(defn subscribe-topic
  "subscribe to topic, put items on specified channel"
  [put-fn conn topic]
  (car/with-new-pubsub-listener
    (:spec conn)
    {topic (msg-handler-fn put-fn)}
    (car/subscribe topic)))

(defn iop-state-fn
  "Returns function for making state of the interop-component.
   Takes :cmp-id and configuration."
  [conf cmp-id]
  (fn [put-fn]
    (let [conn {:pool {}
                :spec (select-keys conf [:host :port])}
          listener (subscribe-topic put-fn conn (:topic conf))]
      (log/info cmp-id "connected to Redis:" (:host conf) (:port conf))
      {:state (atom {:conf     conf
                     :conn     conn
                     :listener listener})})))

(defn cmp-map
  "Create component for communicating with Redis."
  [cmp-id conf]
  {:cmp-id      cmp-id
   :state-fn    (iop-state-fn conf cmp-id)
   :state-spec  :st-redis/store-spec
   :handler-map {}})
