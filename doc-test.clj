(ns
    #^{:author "Andy Kish"
       :doc "Verifies correctness of example expressions in doc-strings."}
  doc-test
  (:use clojure.test)
  (:use [clojure.contrib.str-utils2 :only (split)]))


(defn- adder
  "returns a HOF adding two numbers together

  => ((adder 1) 2)
  3
  => ((adder 4) 5)
  10"
  [n1]
  (fn [n2] (+ n1 n2)))

(defn- read-expr-pair [expr-string]
  (with-open [sreader (new java.io.StringReader expr-string)
              pbreader (new java.io.PushbackReader sreader)]
    [(read pbreader) (read pbreader)]))

(defn- find-expressions [docstr]
  (let [doctest-strings (drop 1 (split docstr #"=>"))]
    (map read-expr-pair doctest-strings)))

(defn to-is [doc]
  (let [exprs (find-expressions doc)]
    (map (fn [[expr result]] `(is (= (eval ~expr) ~result)))
         exprs)))

(defmacro doctest
  [f]
  "Creates a (deftest ...) form based upon the examples in f's doc."
  `(deftest testname#
     ~@(to-is (eval `(:doc (meta (var ~f)))))))


(doctest adder)

; what we're shooting for, approximately
;(deftest adder10386
;  (let [[expr result] (read-expr-pair "((adder 1) 4) 3")]
;    (is (= (eval expr) result))))
