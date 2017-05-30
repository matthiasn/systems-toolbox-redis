(ns matthiasn.systems-toolbox-redis.spec
  (:require [clojure.spec.alpha :as s]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Component State Spec
(s/def :st-redis/store-spec
  (s/keys :req-un [:st-redis/conf
                   :st-redis/conn]
          :opt-un [:st-redis/listener]))
