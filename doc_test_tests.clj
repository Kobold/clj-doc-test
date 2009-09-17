(ns
    doc-test-tests
  (:use clojure.test)
  (:use doc-test))


(run-tests 'doc-test)

(defn adder
  "A simple function to test the doctest macro with.

  => ((adder 1) 2)
  4 ; incorrect!
  => ((adder 4) 5)
  9"
  [n1]
  (fn [n2] (+ n1 n2)))

(doc-test adder)
;(doc-test find-expression-strings)
;(doc-test to-is)
(run-tests)