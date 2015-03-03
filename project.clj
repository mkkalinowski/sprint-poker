(defproject sprint-poker "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[compojure "1.3.2"]
                 [org.clojure/algo.generic "0.1.2"]
                 [org.clojure/clojure "1.6.0"]
                 [org.clojure/data.json "0.2.5"]
                 [ring/ring-core "1.3.2"]
                 [ring-server "0.4.0"]]
  :plugins [[lein-ring "0.9.1"]]
  :ring {:handler sprint-poker.core/handler}
  :main sprint-poker.core
  :aot [sprint-poker.core])
