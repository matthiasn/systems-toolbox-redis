(defproject matthiasn/systems-toolbox-redis "0.6.4"
  :description "Redis communication components for systems-toolbox"
  :url "https://github.com/matthiasn/systems-toolbox-redis"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :source-paths ["src/clj"]

  :test-paths ["test" "test-resources"]

  :dependencies [[org.clojure/tools.logging "0.3.1"]
                 [com.taoensso/carmine "2.16.0" :exclusions [org.clojure/tools.reader]]]

  :profiles {:dev {:dependencies [[org.clojure/clojure "1.9.0-alpha17"]
                                  [matthiasn/systems-toolbox "0.6.9"]]}}

  :plugins [[lein-codox "0.10.3"]
            [lein-ancient "0.6.10"]
            [lein-cloverage "1.0.9"]])
