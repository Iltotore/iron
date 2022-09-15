package io.github.iltotore.iron.testing

import utest.*
import io.github.iltotore.iron.*, constraint.collection.{*, given}

object CollectionSuite extends TestSuite {

  val tests: Tests = Tests {

    test("minLength") {
      test - List(1, 2, 3).assertNotRefine[MinLength[4]]
      test - List(1, 2, 3, 4).assertRefine[MinLength[4]]
    }

    test("maxLength") {
      test - List(1, 2, 3).assertRefine[MaxLength[3]]
      test - List(1, 2, 3, 4).assertNotRefine[MaxLength[3]]
    }

    test("contains") {
      test - List(1, 2, 3).assertRefine[Contain[3]]
      test - List(1, 2, 4).assertNotRefine[Contain[3]]
    }
  }
}
