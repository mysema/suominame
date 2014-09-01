(ns suominame.core
  (:import java.io.File)
  (:require [compojure.handler :as handler]
            [compojure.core :refer [defroutes routes context
                                    ANY GET POST PUT DELETE]]
            [compojure.route :as route]
            [ring.util.response :as resp]
            [ring.middleware.jsonp :as jsonp]
            [ring.middleware.json :as json]
            [clojure.set :as set]
            [clojure.string :refer [split-lines trim] :as string]
            [clojure.java.io :as jio]
            ))

(defn read-resource
  "Return the string contents of the given classpath resource"
  [^String path]
  (->> (slurp (jio/resource path) :encoding "UTF-8")
       split-lines
       (map trim)))

(defn read-config
  "Return the string contents of the given configuration resource or 
   classpath, if the related file doesn't exist"
  [^String path]
  (let [file (File. "resources" path)]
    (if (.exists file)
      (->> (slurp file :encoding "UTF-8")
           split-lines
           (map trim))
      (read-resource path))))

(def male-fnames (set (read-config "miestenetunimet.txt")))
(def female-fnames (set (read-config "naistennimet.txt")))
(def lastnames (set (read-config "sukunimet.txt")))


(defn get-name
  [origname numofstart newnames]
  (let [start (.toLowerCase (apply str (take numofstart origname)))
        matches (filter #(= start (.toLowerCase (apply str (take numofstart %)))) newnames)
        nameind (rand-int (count matches))]
    (first (drop nameind matches))))


(defn some-name
  [origname newnames]
  (let [names2 (get-name origname 2 newnames)
        names1 (get-name origname 1 newnames)
        failsafe (get-name "K" 1 newnames)]
    (or names2 names1 failsafe)))

(not-any? empty? ["s" "h" "sjsjs"])

(defroutes app-routes

  (GET "/suominame" [sex firstname lastname]
       (resp/response
         
         (if (not-any? empty? [sex firstname lastname])
           (let [fnames (if (= "m" (.toLowerCase sex)) male-fnames female-fnames)]
             {:firstname (some-name firstname fnames) 
              :lastname (some-name lastname lastnames)})
             
           {:error "Invalid parameters, some values missing. Provide not null in all of: sex (m|f), firstname and lastname"}
           )))
 
)

(defn wrapper-config
  [routes]
  (-> routes
      json/wrap-json-response
      jsonp/wrap-json-with-padding
      handler/api
      ))

(def handler (wrapper-config app-routes))

(defn start-dev-server
  "Starts Jetty in development mode"
  ([] (start-dev-server false))
  ([auto-reload?]
   (require 'ring.server.standalone)
   (def dev-handler (wrapper-config #'app-routes))
   ((resolve 'ring.server.standalone/serve) (resolve 'dev-handler)
    {:auto-reload?  auto-reload?
     :open-browser? false})))

(comment
  (def server (start-dev-server))
  (.stop server)
  (.start server)
  )


