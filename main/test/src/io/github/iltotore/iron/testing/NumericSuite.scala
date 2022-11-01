package io.github.iltotore.iron.testing
import io.github.iltotore.iron.*, constraint.numeric.{*, given}
import utest.*

object NumericSuite extends TestSuite:

  val tests: Tests = Tests {

    test("greater") {
      test - 0.assertNotRefine[Greater[0]]
      test - 1.assertRefine[Greater[0]]
    }

    test("greaterEqual") {
      test - -1.assertNotRefine[GreaterEqual[0]]
      test - 0.assertRefine[GreaterEqual[0]]
      test - 1.assertRefine[GreaterEqual[1]]
    }

    test("less") {
      test - 0.assertNotRefine[Less[0]]
      test - -1.assertRefine[Less[0]]
    }

    test("lessEqual") {
      test - 1.assertNotRefine[LessEqual[0]]
      test - 0.assertRefine[LessEqual[0]]
      test - -1.assertRefine[LessEqual[0]]
    }

    test("multiple") {
      test - 1.assertNotRefine[Multiple[2]]
      test - 2.assertRefine[Multiple[2]]
    }

    test("divide") {
      test - 1.assertRefine[Divide[2]]
      test - 2.assertRefine[Divide[2]]
      test - 3.assertNotRefine[Divide[2]]
    }
  }
