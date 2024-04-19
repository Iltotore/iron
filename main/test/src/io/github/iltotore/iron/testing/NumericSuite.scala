package io.github.iltotore.iron.testing

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.*
import utest.*

object NumericSuite extends TestSuite:

  val tests: Tests = Tests {

    test("greater"):
      test - 0.assertNotRefine[Greater[0]]
      test - 1.assertRefine[Greater[0]]
      test - BigDecimal(0).assertNotRefine[Greater[0]]
      test - BigDecimal(1).assertRefine[Greater[0]]
      test - BigInt(0).assertNotRefine[Greater[0]]
      test - BigInt(1).assertRefine[Greater[0]]

    test("greaterEqual"):
      test - -1.assertNotRefine[GreaterEqual[0]]
      test - 0.assertRefine[GreaterEqual[0]]
      test - 1.assertRefine[GreaterEqual[1]]

    test("less"):
      test - 0.assertNotRefine[Less[0]]
      test - -1.assertRefine[Less[0]]
      test - BigDecimal(0).assertNotRefine[Less[0]]
      test - BigDecimal(-1).assertRefine[Less[0]]
      test - BigInt(0).assertNotRefine[Less[0]]
      test - BigInt(-1).assertRefine[Less[0]]

    test("lessEqual"):
      test - 1.assertNotRefine[LessEqual[0]]
      test - 0.assertRefine[LessEqual[0]]
      test - -1.assertRefine[LessEqual[0]]

    test("interval") {
      test("open"):
        test - 1.assertRefine[Interval.Open[0, 2]]
        test - 0.assertNotRefine[Interval.Open[0, 2]]
        test - 2.assertNotRefine[Interval.Open[0, 2]]
        test - -1.assertNotRefine[Interval.Open[0, 2]]
        test - 3.assertNotRefine[Interval.Open[0, 2]]

      test("openClosed"):
        test - 1.assertRefine[Interval.OpenClosed[0, 2]]
        test - 2.assertRefine[Interval.OpenClosed[0, 2]]
        test - 0.assertNotRefine[Interval.OpenClosed[0, 2]]
        test - -1.assertNotRefine[Interval.OpenClosed[0, 2]]
        test - 3.assertNotRefine[Interval.OpenClosed[0, 2]]

      test("closedOpen"):
        test - 1.assertRefine[Interval.ClosedOpen[0, 2]]
        test - 0.assertRefine[Interval.ClosedOpen[0, 2]]
        test - 2.assertNotRefine[Interval.ClosedOpen[0, 2]]
        test - -1.assertNotRefine[Interval.ClosedOpen[0, 2]]
        test - 3.assertNotRefine[Interval.ClosedOpen[0, 2]]

      test("closed"):
        test - 1.assertRefine[Interval.Closed[0, 2]]
        test - 2.assertRefine[Interval.Closed[0, 2]]
        test - 0.assertRefine[Interval.Closed[0, 2]]
        test - -1.assertNotRefine[Interval.Closed[0, 2]]
        test - 3.assertNotRefine[Interval.Closed[0, 2]]
    }

    test("multiple"):
      test - 1.assertNotRefine[Multiple[2]]
      test - 2.assertRefine[Multiple[2]]
      test - BigInt(1).assertNotRefine[Multiple[2]]
      test - BigInt(2).assertRefine[Multiple[2]]
      test - BigDecimal(1).assertNotRefine[Multiple[2]]
      test - BigDecimal(2).assertRefine[Multiple[2]]

    test("divide"):
      test - 1.assertRefine[Divide[2]]
      test - 2.assertRefine[Divide[2]]
      test - 3.assertNotRefine[Divide[2]]
      test - BigInt(1).assertRefine[Divide[2]]
      test - BigInt(2).assertRefine[Divide[2]]
      test - BigInt(3).assertNotRefine[Divide[2]]
      test - BigDecimal(1).assertRefine[Divide[2]]
      test - BigDecimal(2).assertRefine[Divide[2]]
      test - BigDecimal(3).assertNotRefine[Divide[2]]

    test("nan"):
      test - Float.NaN.assertRefine[NaN]
      test - Double.NaN.assertRefine[NaN]
      test - 0f.assertNotRefine[NaN]
      test - 0d.assertNotRefine[NaN]

    test("infinity"):
      test - Float.PositiveInfinity.assertRefine[Infinity]
      test - Float.NegativeInfinity.assertRefine[Infinity]
      test - Double.PositiveInfinity.assertRefine[Infinity]
      test - Double.NegativeInfinity.assertRefine[Infinity]
      test - 0f.assertNotRefine[Infinity]
      test - 0d.assertNotRefine[Infinity]
  }
