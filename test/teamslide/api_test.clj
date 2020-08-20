(ns teamslide.api-test
  (:require [clojure.test :refer :all]
            [teamslide.api :refer :all])
  (:import [org.postgis Point PGgeography]))

(deftest test-geo
  (testing "geo parses lat,lng"
    (is (= (geo "42.0,-11.0")
           (PGgeography. (Point. 42.0 -11.0))))))

(def fake-leagues
  [{:id 0 :price 2}
   {:id 1 :price 10}
   {:id 2 :price 7}
   {:id 3 :price 4}
   {:id 4 :price 3}])

(defn fake-league-ids-within [budget]
  (into #{} (map :id (most-leagues-within-budget budget fake-leagues))))

(deftest test-most-leagues-within-budget
  (testing "takes leagues until the budget is exceeded"
    (is (= (fake-league-ids-within 10)
           #{0 3 4})))
  (testing "takes all leagues if budget is not exceeded"
    (is (= (fake-league-ids-within 10000)
           #{0 1 2 3 4})))
  (testing "takes no leagues if all are over budget"
    (is (= (fake-league-ids-within 1)
           #{})))
  (testing "handles empty and nil gracefully"
    (is (= (most-leagues-within-budget 2 []) []))))

