package io.github.iltotore.iron.testing

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import utest.*

object StringSuite extends TestSuite:

  val tests: Tests = Tests {

    test("blank"):
      test - "".assertRefine[Blank]
      test - " \t\n\u000B\f\r\u001C\u001D\u001E\u001F".assertRefine[Blank]
      test - "a".assertNotRefine[Blank]

    test("trimmed"):
      test - "".assertRefine[Trimmed]
      test - "abc".assertRefine[Trimmed]
      test - " ".assertNotRefine[Trimmed]
      test - " abc ".assertNotRefine[Trimmed]
      test - "abc\n".assertNotRefine[Trimmed]

    test("lowercase"):
      test - "abc 123 \n".assertRefine[LettersLowerCase]
      test - "ABC 123 \n".assertNotRefine[LettersLowerCase]

    test("uppercase"):
      test - "abc 123 \n".assertNotRefine[LettersUpperCase]
      test - "ABC 123 \n".assertRefine[LettersUpperCase]

    test("alphanumeric"):
      test - "abc".assertRefine[Alphanumeric]
      test - "123".assertRefine[Alphanumeric]
      test - "abc123".assertRefine[Alphanumeric]
      test - "".assertRefine[Alphanumeric]
      test - "abc123_".assertNotRefine[Alphanumeric]
      test - " ".assertNotRefine[Alphanumeric]

    test("startWith"):
      test - "abc".assertRefine[StartWith["abc"]]
      test - "abc123".assertRefine[StartWith["abc"]]
      test - "ab".assertNotRefine[StartWith["abc"]]

    test("endWith"):
      test - "abc".assertRefine[EndWith["abc"]]
      test - "123abc".assertRefine[EndWith["abc"]]
      test - "ab".assertNotRefine[EndWith["abc"]]

    test("match"):
      test - "998".assertRefine[Match["[0-9]+"]]
      test - "abc".assertNotRefine[Match["[0-9]+"]]
      test - "".assertNotRefine[Match["[0-9]+"]]

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
      test - "https://aaaaa-bbb-cccccc-dddddddd.eeeeeeee-fff.ggg.hhhhhhhhh:1234".assertRefine[ValidURL]
      test - "/example.com".assertNotRefine[ValidURL]
      test - "://example.com".assertNotRefine[ValidURL]
      test - "http:///".assertNotRefine[ValidURL]
    }

    test("semantic version") {
      test - "1.0.0".assertRefine[SemanticVersion]
      test - "1.0.0-alpha".assertRefine[SemanticVersion]
      test - "1.0.0-alpha.1".assertRefine[SemanticVersion]
      test - "1.0.0-0.3.7".assertRefine[SemanticVersion]
      test - "1.0.0-x.7.z.92".assertRefine[SemanticVersion]
      test - "1.0.0-alpha+001".assertRefine[SemanticVersion]
      test - "1.0.0+20130313144700".assertRefine[SemanticVersion]
      test - "1.0.0-beta+exp.sha.5114f85".assertRefine[SemanticVersion]
      test - "1.0.0-rc.1+exp.sha.5114f85".assertRefine[SemanticVersion]
      test - "1".assertNotRefine[SemanticVersion]
      test - "1.0".assertNotRefine[SemanticVersion]
      test - "x.0.0".assertNotRefine[SemanticVersion]
      test - "0.y.0".assertNotRefine[SemanticVersion]
      test - "0.0.z".assertNotRefine[SemanticVersion]
      test - "x.y.z".assertNotRefine[SemanticVersion]
      test - "00000001.0.0".assertNotRefine[SemanticVersion]
      test - "0.00000001.0".assertNotRefine[SemanticVersion]
      test - "0.0.00000001".assertNotRefine[SemanticVersion]
      test - "1.0.0-rc.1+exp.sha.51_14f85".assertNotRefine[SemanticVersion]
      test - "1.0.0-rc.1+exp.sha.51 14f85".assertNotRefine[SemanticVersion]
      test - "1.0.0-rc.1+exp.sha.51?14f85".assertNotRefine[SemanticVersion]
      test - "1.0.0-rc.1+exp.sha.51#14f85".assertNotRefine[SemanticVersion]
      test - "1.0.0-rc.1+exp.sha.51@14f85".assertNotRefine[SemanticVersion]
      test - "1.0.0-rc.1+exp.sha.51:14f85".assertNotRefine[SemanticVersion]
      test - "1.0.0-rc.1+exp.sha.51*14f85".assertNotRefine[SemanticVersion]
      test - "1.0.0-rc.1+exp.sha.51|14f85".assertNotRefine[SemanticVersion]
    }

  }
