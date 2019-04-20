(defproject fx-streamer "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.516"]
                 [io.vertx/vertx-core "3.7.0"]
                 [io.vertx/vertx-hazelcast "3.7.0"]
                 [io.vertx/vertx-web "3.7.0"]
                 [io.vertx/vertx-rx-java2 "3.7.0"]
                 [reagent "0.8.1"]
                 [re-frame "0.10.6"]]

  :source-paths ["src/main/cljs" "src/main/java"  "src/main/java"]
  :resource-paths ["src/main/resources"]

  :aliases {"fig"       ["trampoline" "run" "-m" "figwheel.main"]
            "fig:build" ["trampoline" "run" "-m" "figwheel.main" "-b" "dev" "-r"]
            "fig:min"   ["run" "-m" "figwheel.main" "-O" "advanced" "-bo" "dev"]
            "fig:test"  ["run" "-m" "figwheel.main" "-co" "test.cljs.edn" "-m" user-shell.test-runner]}

  :profiles {:dev {:dependencies [[com.bhauman/figwheel-main "0.1.9"]
                                  [org.junit.jupiter/junit-jupiter-api "5.5.0-M1"]
                                  [com.bhauman/rebel-readline-cljs "0.1.4"]]}})
