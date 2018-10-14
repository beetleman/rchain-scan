(def protoc-grpc-version "1.15.1")
(def protoc-version "3.6.1")
(def proto-target-path "target/generated")

(defproject rchain-scan "0.1.0-SNAPSHOT"

  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :dependencies [[baking-soda "0.2.0" :exclusions [cljsjs/react-bootstrap]]
                 [cljs-ajax "0.7.4"]
                 [cljsjs/react-popper "0.10.4-0"]
                 [cljsjs/react-transition-group "2.4.0-0"]
                 [clojure.java-time "0.3.2"]
                 [com.cognitect/transit-clj "0.8.313"]
                 [com.fasterxml.jackson.core/jackson-core "2.9.7"]
                 [com.fasterxml.jackson.datatype/jackson-datatype-jdk8 "2.9.7"]
                 [com.h2database/h2 "1.4.197"]
                 [conman "0.8.2"]
                 [cprop "0.1.13"]
                 [funcool/struct "1.3.0"]
                 [kee-frame "0.3.0"]
                 [luminus-immutant "0.2.4"]
                 [luminus-migrations "0.5.5"]
                 [luminus-transit "0.1.1"]
                 [luminus/ring-ttl-session "0.3.2"]
                 [markdown-clj "1.0.3"]
                 [metosin/muuntaja "0.6.1"]
                 [metosin/reitit "0.2.3"]
                 [metosin/ring-http-response "0.9.0"]
                 [mount "0.1.13"]
                 [nrepl "0.4.5"]
                 [org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.339" :scope "provided"]
                 [org.clojure/java.jdbc "0.7.8"]
                 [org.clojure/tools.cli "0.4.1"]
                 [org.clojure/tools.logging "0.4.1"]
                 [org.webjars.bower/tether "1.4.4"]
                 [org.webjars/bootstrap "4.1.3"]
                 [org.webjars/font-awesome "5.3.1"]
                 [org.webjars/webjars-locator "0.34"]
                 [com.google.guava/guava "26.0-jre"]
                 [re-frame "0.10.6"]
                 [reagent "0.8.1"]
                 [ring-webjars "0.2.0"]
                 [ring/ring-core "1.7.0"]
                 [ring/ring-defaults "0.3.2"]
                 [secretary "1.2.3"]
                 [selmer "1.12.2"]

                 [io.grpc/grpc-netty-shaded ~protoc-grpc-version :exclusions [io.grpc/grpc-core]]
                 [io.grpc/grpc-core ~protoc-grpc-version]
                 [io.grpc/grpc-netty-shaded ~protoc-grpc-version]
                 [io.grpc/grpc-protobuf ~protoc-grpc-version]
                 [io.grpc/grpc-stub ~protoc-grpc-version]]

  :min-lein-version "2.0.0"

  :source-paths ["src/clj" "src/cljs" "src/cljc"]
  :test-paths ["test/clj"]
  :resource-paths ["resources" "target/cljsbuild"]
  :target-path "target/%s/"
  :main ^:skip-aot rchain-scan.core

  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-immutant "2.1.0"]
            [lein-ancient "0.6.15"]
            [lein-protoc "0.5.0"]
            [lein-kibit "0.1.6"]]

  :protoc-grpc {:version ~protoc-grpc-version}
  :protoc-version ~protoc-version
  :proto-target-path ~proto-target-path
  :java-source-paths [~proto-target-path]

  :clean-targets ^{:protect false}
  [:target-path [:cljsbuild :builds :app :compiler :output-dir] [:cljsbuild :builds :app :compiler :output-to]]
  :figwheel
  {:http-server-root "public"
   :server-logfile "log/figwheel-logfile.log"
   :nrepl-port 7002
   :css-dirs ["resources/public/css"]
   :nrepl-middleware
   [cider/wrap-cljs-repl cider.piggieback/wrap-cljs-repl]}


  :profiles
  {:uberjar {:omit-source true
             :prep-tasks ["javac" "compile" ["cljsbuild" "once" "min"]]
             :cljsbuild
             {:builds
              {:min
               {:source-paths ["src/cljc" "src/cljs" "env/prod/cljs"]
                :compiler
                {:output-dir "target/cljsbuild/public/js"
                 :output-to "target/cljsbuild/public/js/app.js"
                 :source-map "target/cljsbuild/public/js/app.js.map"
                 :optimizations :advanced
                 :pretty-print false
                 :infer-externs true
                 :closure-warnings
                 {:externs-validation :off :non-standard-jsdoc :off}
                 :externs ["react/externs/react.js"]}}}}


             :aot :all
             :uberjar-name "rchain-scan.jar"
             :source-paths ["env/prod/clj"]
             :resource-paths ["env/prod/resources"]}

   :dev           [:project/dev :profiles/dev]
   :test          [:project/dev :project/test :profiles/test]

   :project/dev  {:jvm-opts ["-Dconf=dev-config.edn"]
                  :dependencies [[binaryage/devtools "0.9.10"]
                                 [cider/piggieback "0.3.9"]
                                 [day8.re-frame/re-frame-10x "0.3.3-react16"]
                                 [doo "0.1.10"]
                                 [expound "0.7.1"]
                                 [figwheel-sidecar "0.5.16"]
                                 [pjstadig/humane-test-output "0.8.3"]
                                 [prone "1.6.1"]
                                 [ring/ring-devel "1.7.0"]
                                 [ring/ring-mock "0.3.2"]]
                  :plugins      [[com.jakemccrary/lein-test-refresh "0.23.0"]
                                 [lein-doo "0.1.10"]
                                 [lein-figwheel "0.5.16"]]
                  :cljsbuild
                  {:builds
                   {:app
                    {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
                     :figwheel {:on-jsload "rchain-scan.core/mount-components"}
                     :compiler
                     {:main "rchain-scan.app"
                      :asset-path "/js/out"
                      :output-to "target/cljsbuild/public/js/app.js"
                      :output-dir "target/cljsbuild/public/js/out"
                      :source-map true
                      :optimizations :none
                      :pretty-print true
                      :closure-defines {"re_frame.trace.trace_enabled_QMARK_" true}
                      :preloads [day8.re-frame-10x.preload]}}}}



                  :doo {:build "test"}
                  :source-paths ["env/dev/clj"]
                  :resource-paths ["env/dev/resources"]
                  :repl-options {:init-ns user}
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]}
   :project/test {:jvm-opts ["-Dconf=test-config.edn"]
                  :resource-paths ["env/test/resources"]
                  :cljsbuild
                  {:builds
                   {:test
                    {:source-paths ["src/cljc" "src/cljs" "test/cljs"]
                     :compiler
                     {:output-to "target/test.js"
                      :main "rchain-scan.doo-runner"
                      :optimizations :whitespace
                      :pretty-print true}}}}

                  }
   :profiles/dev {}
   :profiles/test {}})
