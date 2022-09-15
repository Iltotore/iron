package io.github.iltotore.iron.testing

import utest.*
import io.github.iltotore.iron.*, constraint.string.{*, given}

object StringSuite extends TestSuite {

  val tests: Tests = Tests {

    test("minLength") {
      test - "abc".assertNotRefine[MinLength[4]]
      test - "abcd".assertRefine[MinLength[4]]
    }

    test("maxLength") {
      test - "abc".assertRefine[MaxLength[3]]
      test - "abcd".assertNotRefine[MaxLength[3]]
    }

    test("contains") {
      test - "abc".assertRefine[Contain["c"]]
      test - "abd".assertNotRefine[Contain["c"]]
    }

    test("lowercase") {
      test - "abc".assertRefine[LowerCase]
      test - "ABC".assertNotRefine[LowerCase]
    }

    test("uppercase") {
      test - "abc".assertNotRefine[UpperCase]
      test - "ABC".assertRefine[UpperCase]
    }

    test("match") {
      test - "998".assertRefine[Match["[0-9]+"]]
      test - "abc".assertNotRefine[Match["[0-9]+"]]
      test - "".assertNotRefine[Match["[0-9]+"]]
    }
  }
}
