(defproject teamslide "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/java.jdbc "0.7.11"]
                 [com.layerware/hugsql "0.5.1"]
                 [org.postgresql/postgresql "42.2.15"]
                 [net.postgis/postgis-jdbc "2.5.0"]
                 [ring/ring-jetty-adapter "1.8.1"]
                 [ring/ring-defaults "0.3.2"]
                 [compojure "1.6.2"]
                 [cheshire "5.10.0"]]
  :main ^:skip-aot teamslide.api 
  :uberjar-name "teamslide-standalone.jar"
  :plugins [[lein-ring "0.8.13"]]
  :ring {:handler teamslide.api/app
         :init teamslide.api/init}
  :profiles {:uberjar {:aot :all}})

