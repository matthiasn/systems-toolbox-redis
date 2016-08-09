(ns matthiasn.systems-toolbox-redis.sender
  (:require [matthiasn.systems-toolbox-redis.spec]
            [taoensso.carmine :as car]
            [clojure.tools.logging :as l]))

(defn publish
  "Publish tweet with matches on Redis Pub/Sub for specified topic."
  [conn topic msg]
  (car/wcar conn (car/publish topic msg)))

(defn iop-state-fn
  "Returns function for making state of the interop-component while using
   provided configuration."
  [conf cmp-id]
  (fn [_put-fn]
    (let [conn {:pool {}
                :spec (select-keys conf [:host :port])}]
      (l/info cmp-id "connected to Redis:" (:host conf) (:port conf))
      {:state (atom {:conf conf
                     :conn conn})})))

(defn publish-msg
  "Publish message on Redis topic."
  [{:keys [current-state msg-type msg-payload msg-meta]}]
  (publish (:conn current-state)
           (:topic (:conf current-state))
           [msg-type {:msg msg-payload :msg-meta msg-meta}]))

(defn cmp-map
  "Create component for communicating via Redis."
  [cmp-id conf]
  (merge
    {:cmp-id     cmp-id
     :state-fn   (iop-state-fn conf cmp-id)
     :state-spec :st-redis/store-spec}
    (if-let [msg-types (:relay-types conf)]
      {:handler-map (zipmap msg-types (repeat publish-msg))}
      (do
        (l/warn "using redis-cmp without specified :relay-types not recommended")
        {:all-msgs-handler publish-msg}))))
