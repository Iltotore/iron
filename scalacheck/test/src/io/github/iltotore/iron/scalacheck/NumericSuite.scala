package io.github.iltotore.iron.scalacheck

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.*
import io.github.iltotore.iron.scalacheck.numeric.given
import io.github.iltotore.iron.scalacheck.any.strictEqual
import org.scalacheck.*
import utest.*

object NumericSuite extends TestSuite:

  val tests: Tests = Tests {

    test("greater"):
      test("int") - testGen[Int, Greater[5]]
      test("long") - testGen[Long, Greater[5L]]
      test("float") - testGen[Float, Greater[5f]]
      test("double") - testGen[Double, Greater[5d]]

    test("greaterEqual"):
      test("int") - testGen[Int, GreaterEqual[5]]
      test("long") - testGen[Long, GreaterEqual[5L]]
      test("float") - testGen[Float, GreaterEqual[5f]]
      test("double") - testGen[Double, GreaterEqual[5d]]

    test("less"):
      test("int") - testGen[Int, Less[5]]
      test("long") - testGen[Long, Less[5L]]
      test("float") - testGen[Float, Less[5f]]
      test("double") - testGen[Double, Less[5d]]

    test("lessEqual"):
      test("int") - testGen[Int, LessEqual[5]]
      test("long") - testGen[Long, LessEqual[5L]]
      test("float") - testGen[Float, LessEqual[5f]]
      test("double") - testGen[Double, LessEqual[5d]]

    test("strictEqual"):
      test("int") - testGen[Int, StrictEqual[5]]
      test("long") - testGen[Long, StrictEqual[5L]]
      test("float") - testGen[Float, StrictEqual[5f]]
      test("double") - testGen[Double, StrictEqual[5d]]

    test("interval") {
      test("open"):
        test("int") - testGen[Int, Interval.Open[0, 5]]
        test("long") - testGen[Long, Interval.Open[0L, 5L]]
        test("float") - testGen[Float, Interval.Open[0f, 5f]]
        test("double") - testGen[Double, Interval.Open[0d, 5d]]

      test("openClosed"):
        test("int") - testGen[Int, Interval.OpenClosed[0, 5]]
        test("long") - testGen[Long, Interval.OpenClosed[0L, 5L]]
        test("float") - testGen[Float, Interval.OpenClosed[0f, 5f]]
        test("double") - testGen[Double, Interval.OpenClosed[0d, 5d]]

      test("closedOpen"):
        test("int") - testGen[Int, Interval.ClosedOpen[0, 5]]
        test("long") - testGen[Long, Interval.ClosedOpen[0L, 5L]]
        test("float") - testGen[Float, Interval.ClosedOpen[0f, 5f]]
        test("double") - testGen[Double, Interval.ClosedOpen[0d, 5d]]

      test("closed"):
        test("int") - testGen[Int, Interval.Closed[0, 5]]
        test("long") - testGen[Long, Interval.Closed[0L, 5L]]
        test("float") - testGen[Float, Interval.Closed[0f, 5f]]
        test("double") - testGen[Double, Interval.Closed[0d, 5d]]
    }
  }
