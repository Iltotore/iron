package io.github.iltotore.iron.testing

import io.github.iltotore.iron.*
import utest.*

object AnySuite extends TestSuite:

  val tests: Tests = Tests {

    test("constant") {
      test("true") - Dummy.assertRefine[True]
      test("false") - Dummy.assertNotRefine[False]
    }

    test("describedAs") {
      test - Dummy.assertRefine[True DescribedAs "test"]
      test - Dummy.assertNotRefine[False DescribedAs "test"]
    }

    test("not") {
      test - Dummy.assertNotRefine[Not[True]]
      test - Dummy.assertRefine[Not[False]]
    }

    test("union") {
      test - Dummy.assertRefine[True | True]
      test - Dummy.assertRefine[True | False]
      test - Dummy.assertNotRefine[False | False]
    }

    test("and") {
      test - Dummy.assertRefine[True & True]
      test - Dummy.assertNotRefine[True & False]
      test - Dummy.assertNotRefine[False & False]
    }

    test("strictEqual") {
      test - 0.assertRefine[StrictEqual[0]]
      test - 1.assertNotRefine[StrictEqual[0]]
    }
  }
