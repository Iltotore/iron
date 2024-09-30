package io.github.iltotore.iron

import utest.*
import scala.math.Numeric.IntIsIntegral
import io.github.iltotore.iron.constraint.numeric.*

object ShiftSuite extends TestSuite:

  def assertShift[A, C](value: A, expected: A :| C)(using bounds: Bounds[A, C]): Unit =
    assert(bounds.shift(value) == expected)

  val tests: Tests = Tests:
    test("closed"):
      test("inBounds") - assertShift[Int, Interval.Closed[0, 10]](5, 5)
      test("upper") - assertShift[Int, Interval.Closed[0, 10]](11, 0)
      test("lower") - assertShift[Int, Interval.Closed[0, 10]](-1, 10)
    
    test("positive"):
      test("inBounds") - assertShift[Int, Positive](5, 5)
      test("int") - assertShift[Int, Positive](0, Int.MaxValue)
      test("long") - assertShift[Long, Positive](0, Long.MaxValue)
      test("float") - assertShift[Float, Positive](0, Float.MaxValue)
      test("double") - assertShift[Double, Positive](0, Double.MaxValue)

    test("positive0"):
      test("inBounds"):
        test - assertShift[Int, Positive0](5, 5)
        test - assertShift[Int, Positive0](0, 0)
      test("int") - assertShift[Int, Positive0](-1, Int.MaxValue)
      test("long") - assertShift[Long, Positive0](-1, Long.MaxValue)
      test("float") - assertShift[Float, Positive0](-1, Float.MaxValue)
      test("double") - assertShift[Double, Positive0](-1, Double.MaxValue)

    test("negative"):
      test("inBounds") - assertShift[Int, Negative](-5, -5)
      test("int") - assertShift[Int, Negative](0, Int.MinValue)
      test("long") - assertShift[Long, Negative](0, Long.MinValue)
      test("float") - assertShift[Float, Negative](0, Float.MinValue)
      test("double") - assertShift[Double, Negative](0, Double.MinValue)

    test("negative0"):
      test("inBounds"):
        test - assertShift[Int, Negative0](-5, -5)
        test - assertShift[Int, Negative0](0, 0)
      test("int") - assertShift[Int, Negative0](1, Int.MinValue)
      test("long") - assertShift[Long, Negative0](1, Long.MinValue)
      test("float") - assertShift[Float, Negative0](1, Float.MinValue)
      test("double") - assertShift[Double, Negative0](1, Double.MinValue)