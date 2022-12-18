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
      test - "abc 123 \n".assertRefine[LowerCase]
      test - "ABC 123 \n".assertNotRefine[LowerCase]
    }

    test("uppercase") {
      test - "abc 123 \n".assertNotRefine[UpperCase]
      test - "ABC 123 \n".assertRefine[UpperCase]
    }

    test("match") {
      test - "998".assertRefine[Match["[0-9]+"]]
      test - "abc".assertNotRefine[Match["[0-9]+"]]
      test - "".assertNotRefine[Match["[0-9]+"]]
    }

    test("url") {
      test - "localhost".assertRefine[URLLike]
      test - "localhost:8080".assertRefine[URLLike]
      test - "example.com".assertRefine[URLLike]
      test - "example.com:8080".assertRefine[URLLike]
      test - "http://example.com/".assertRefine[URLLike]
      test - "https://example.com/".assertRefine[URLLike]
      test - "file://example.com/".assertRefine[URLLike]
      test - "mysql:jdbc://example.com/".assertRefine[URLLike]
      test - "http://example.com/index.html".assertRefine[URLLike]
      test - "http://example.com/#section".assertRefine[URLLike]
      test - "http://example.com/?q=with%20space".assertRefine[URLLike]
      test - "http://example.com/?q=with+space".assertRefine[URLLike]
      test - "/example.com".assertNotRefine[URLLike]
      test - "://example.com".assertNotRefine[URLLike]
      test - "http:///".assertNotRefine[URLLike]
    }

    test("blank") {
      test - "".assertRefine[Blank]
      test - " ".assertRefine[Blank]
      test - " \n \t ".assertRefine[Blank]
      test - "foo ".assertNotRefine[Blank]
      test - " foo".assertNotRefine[Blank]
    }
  }
