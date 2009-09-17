(ns
    #^{:author "Andy Kish"
       :doc "Verifies correctness of example expressions in doc-strings."}
  doc-test
  (:use clojure.test)
  (:use [clojure.contrib.str-utils :only (re-split)]))

(defn- read-expr-pair
  "Read two expressions from expr-string and return a tuple of them.

  => (read-expr-pair \"(+ 1 2) 3\")
  [(+ 1 2) 3]"
  [expr-string]
  (with-open [sreader (new java.io.StringReader expr-string)
              pbreader (new java.io.PushbackReader sreader)]
    [(read pbreader) (read pbreader)]))

(defn- find-expression-strings
  "Finds expressions that they belong in a REPL. Namely, the => arrow
  beginning a line followed by 2 expressions.

  => (find-expression-strings \"=> ((adder 1) 2) 3\")
  (\" ((adder 1) 2) 3\")"
  [docstr]
  (drop 1 (re-split #"\n\s*=>" docstr)))

(defn to-is
  "Converts a doc-test to forms using clojure.test/is."
  [doc]
  (let [expr-strs (find-expression-strings doc)
        exprs (map read-expr-pair expr-strs)]
    (map (fn [[expr result]] `(is (= (eval ~expr) ~result)))
         exprs)))

(defmacro doc-test
  "Creates a (deftest ...) form based upon the examples in f's doc."
  [f]
  (let [testname (gensym "doctest-")
        doc-string (eval `(:doc (meta (var ~f))))
        is-statments (to-is doc-string)]
    (if (seq is-statments) ; only make a test is there are doc-tests
      `(deftest ~testname
         ~@is-statments))))

; what the doc-test macro output is shooting for, approximately
;(deftest doctest-adder-blah-blah
;  (let [[expr result] (read-expr-pair "((adder 1) 4) 3")]
;    (is (= (eval expr) result))))

(doc-test read-expr-pair)
;(doc-test find-expression-strings)
;(doc-test to-is)