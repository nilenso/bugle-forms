(defproject bugle-forms "latest"
  :description "Simple self-hostable forms"
  :url "https://github.com/nilenso/bugle-forms"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[aero "1.1.6"]
                 [bidi "2.1.6"]
                 [buddy/buddy-hashers "1.8.158"]
                 [camel-snake-kebab "0.4.2"]
                 [com.fzakaria/slf4j-timbre "0.3.21"]
                 [com.github.seancorfield/next.jdbc "1.2.761"]
                 [hiccup "1.0.5"]
                 [migratus "1.3.6"]
                 [mount "0.1.16"]
                 [org.clojure/clojure "1.10.3"]
                 [org.clojure/test.check "0.9.0"]
                 [org.postgresql/postgresql "42.3.2"]
                 [ring/ring-core "1.9.5"]
                 [ring/ring-jetty-adapter "1.9.5"]]
  :plugins [[lein-cloverage "1.2.2"]]
  :aliases {"migrations" ["run" "-m" "bugle-forms.migrations/cmd-migrate"]}
  :main ^:skip-aot bugle-forms.app
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}}
  :resource-paths ["config" "resources"])
