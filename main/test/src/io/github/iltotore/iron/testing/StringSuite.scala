package io.github.iltotore.iron.testing

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import utest.*

object StringSuite extends TestSuite:

  val tests: Tests = Tests {

    test("blank") {
      test - "".assertRefine[Blank]
      test - " \t\n\u000B\f\r\u001C\u001D\u001E\u001F".assertRefine[Blank]
      test - "a".assertNotRefine[Blank]
    }

    test("trimmed") {
      test - "".assertRefine[Trimmed]
      test - "abc".assertRefine[Trimmed]
      test - " ".assertNotRefine[Trimmed]
      test - " abc ".assertNotRefine[Trimmed]
      test - "abc\n".assertNotRefine[Trimmed]
    }

    test("lowercase") {
      test - "abc 123 \n".assertRefine[LettersLowerCase]
      test - "ABC 123 \n".assertNotRefine[LettersLowerCase]
    }

    test("uppercase") {
      test - "abc 123 \n".assertNotRefine[LettersUpperCase]
      test - "ABC 123 \n".assertRefine[LettersUpperCase]
    }

    test("alphanumeric") {
      test - "abc".assertRefine[Alphanumeric]
      test - "123".assertRefine[Alphanumeric]
      test - "abc123".assertRefine[Alphanumeric]
      test - "".assertRefine[Alphanumeric]
      test - "abc123_".assertNotRefine[Alphanumeric]
      test - " ".assertNotRefine[Alphanumeric]
    }

    test("startWith") {
      test - "abc".assertRefine[StartWith["abc"]]
      test - "abc123".assertRefine[StartWith["abc"]]
      test - "ab".assertNotRefine[StartWith["abc"]]
    }

    test("endWith") {
      test - "abc".assertRefine[EndWith["abc"]]
      test - "123abc".assertRefine[EndWith["abc"]]
      test - "ab".assertNotRefine[EndWith["abc"]]
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
