(defproject tapahtumatieto "0.1.0"
  :description "Suomi names"
  :url ""
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring/ring-core "1.2.2"]
                 [ring/ring-json "0.3.1"]
                 [compojure "1.1.5"]
                 [ring.middleware.jsonp "0.1.5"] 
                 ]
  :profiles {:dev {:dependencies [[ring-server "0.2.8"]]}}

  :plugins [[lein-ring "0.8.6"]
            [lein-pprint "1.1.1"]
            ]
  :ring {:handler suominame.core/handler})

