package io.github.iltotore.iron.testing

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import utest.*

object StringSuite extends TestSuite:

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
      test - "abc 123 \n".assertRefine[LettersLowerCase]
      test - "ABC 123 \n".assertNotRefine[LettersLowerCase]
    }

    test("uppercase") {
      test - "abc 123 \n".assertNotRefine[LettersUpperCase]
      test - "ABC 123 \n".assertRefine[LettersUpperCase]
    }

    test("match") {
      test - "998".assertRefine[Match["[0-9]+"]]
      test - "abc".assertNotRefine[Match["[0-9]+"]]
      test - "".assertNotRefine[Match["[0-9]+"]]
    }

    test("url") {
      test - "localhost".assertRefine[ValidURL]
      test - "localhost:8080".assertRefine[ValidURL]
      test - "example.com".assertRefine[ValidURL]
      test - "example.com:8080".assertRefine[ValidURL]
      test - "http://example.com/".assertRefine[ValidURL]
      test - "https://example.com/".assertRefine[ValidURL]
      test - "file://example.com/".assertRefine[ValidURL]
      test - "mysql:jdbc://example.com/".assertRefine[ValidURL]
      test - "http://example.com/index.html".assertRefine[ValidURL]
      test - "http://example.com/#section".assertRefine[ValidURL]
      test - "http://example.com/?q=with%20space".assertRefine[ValidURL]
      test - "http://example.com/?q=with+space".assertRefine[ValidURL]
      test - "/example.com".assertNotRefine[ValidURL]
      test - "://example.com".assertNotRefine[ValidURL]
      test - "http:///".assertNotRefine[ValidURL]
    }

  }
