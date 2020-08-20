(ns teamslide.api
  (:require [compojure.core :refer [defroutes GET POST]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [cheshire.core :refer [encode]]
            [cheshire.generate :refer [add-encoder encode-str]]
            [hugsql.core :refer [def-db-fns]]
            [clojure.string :refer [split]])
  (:import [org.postgis Point PGgeography])
  (:gen-class))

(def-db-fns "teamslide/leagues.sql")
(add-encoder org.postgis.PGgeography encode-str)

(def port (Integer. (or (System/getenv "PORT") "8080")))
(def db (or (System/getenv "DATABASE_URL")
            "postgresql://localhost:5432/teamslide"))

(def parse-int #(Integer/parseInt %))
(def parse-double #(Double/parseDouble %))
(def miles->meters (partial * 1609.34))

(defn geo [lat-lng-str]
  (->> (split lat-lng-str #",")
       (take 2)
       (map parse-double)
       (apply #(PGgeography. (Point. %1 %2)))))

(defn sanitize-league [league]
  (-> league
      (update :price parse-int)
      (update :location geo)))

(defn sanitize-search [search]
  (-> search
      (update :radius (comp miles->meters parse-int))
      (update :location geo)
      (update :budget parse-int)))

(defn most-leagues-within-budget
  "Returns as many leagues as possible within budget by selecting the 
  lowest priced leagues until the budget is exceeded"
  [budget leagues]
  (loop [[league & leagues] (sort-by :price leagues)
         sum 0
         result []]
    (if-let [price (:price league)]
      (cond
        (> (+ sum price) budget) result
        (empty? leagues) (conj result league)
        :else (recur leagues (+ sum price) (conj result league)))
      result)))

(defroutes routes
  (POST "/league" [& league] 
    (encode (insert-league db (sanitize-league league))))
  (GET "/search" [& search-params] 
    (->> (sanitize-search search-params)
         (all-leagues-within-radius-budget db)
         (most-leagues-within-budget (-> search-params :budget parse-int))
         (encode))))

(def app (wrap-defaults routes api-defaults))

(defn init [] (create-leagues-table db))

(defn -main []
  (init)
  (run-jetty app {:port port :join? false}))

