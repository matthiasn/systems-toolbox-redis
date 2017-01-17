(ns matthiasn.systems-toolbox-redis.sender
  (:require [matthiasn.systems-toolbox-redis.spec]
            [taoensso.carmine :as car]
            [clojure.tools.logging :as l]
            [clojure.tools.logging :as log]))

(defn iop-state-fn
  "Returns function for making state of the interop-component while using
   provided configuration."
  [conf cmp-id]
  (fn [_put-fn]
    (let [conn {:pool {}
                :spec (select-keys conf [:host :port])}]
      (l/debug cmp-id "Connecting to Redis:" (:host conf) (:port conf))
      {:state (atom {:conf conf
                     :conn conn})})))

(defn publish-msg
  "Publish message on Redis topic."
  [{:keys [current-state msg-type msg-payload msg-meta]}]
  (let [conn (:conn current-state)
        topic (:topic (:conf current-state))
        msg [msg-type {:msg msg-payload :msg-meta msg-meta}]]
    (log/debug "Publishing message on Redis topic" topic msg)
    (car/wcar conn (car/publish topic msg))
    {}))

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
