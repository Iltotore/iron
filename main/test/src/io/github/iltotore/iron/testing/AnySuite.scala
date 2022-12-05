package io.github.iltotore.iron.testing

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.operators.Not
import utest.*

object AnySuite extends TestSuite:

  val tests: Tests = Tests {

    test("describedAs") {
      test - Dummy.assertRefine[Literal[true] DescribedAs "test"]
      test - Dummy.assertNotRefine[Literal[false] DescribedAs "test"]
    }

    test("not") {
      test - Dummy.assertNotRefine[Not[Literal[true]]]
      test - Dummy.assertRefine[Not[Literal[false]]]
    }

    test("union") {
      test - Dummy.assertRefine[Literal[true] | Literal[true]]
      test - Dummy.assertRefine[Literal[true] | Literal[false]]
      test - Dummy.assertNotRefine[Literal[false] | Literal[false]]
    }

    test("and") {
      test - Dummy.assertRefine[Literal[true] & Literal[true]]
      test - Dummy.assertNotRefine[Literal[true] & Literal[false]]
      test - Dummy.assertNotRefine[Literal[false] & Literal[false]]
    }

    test("strictEqual") {
      test - 0.assertRefine[StrictEqual[0]]
      test - 1.assertNotRefine[StrictEqual[0]]
    }
  }
