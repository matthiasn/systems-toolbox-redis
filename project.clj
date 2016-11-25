(defproject matthiasn/systems-toolbox-redis "0.6.2"
  :description "Redis communication components for systems-toolbox"
  :url "https://github.com/matthiasn/systems-toolbox-redis"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :source-paths ["src/clj"]

  :dependencies [[org.clojure/tools.logging "0.3.1"]
                 [com.taoensso/carmine "2.15.0"]]

  :profiles {:dev {:dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                                  [matthiasn/systems-toolbox "0.6.2"]]}}

  :plugins [[lein-codox "0.10.2"]
            [lein-cloverage "1.0.9"]])
