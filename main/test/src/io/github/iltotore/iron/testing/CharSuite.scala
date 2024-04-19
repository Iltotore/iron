package io.github.iltotore.iron.testing

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import utest.*

object CharSuite extends TestSuite:

  val tests: Tests = Tests {

    test("blank"): // See https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Character.html#isWhitespace(char)
      test - ' '.assertRefine[Whitespace]
      test - '\t'.assertRefine[Whitespace]
      test - '\n'.assertRefine[Whitespace]
      test - '\u000B'.assertRefine[Whitespace]
      test - '\f'.assertRefine[Whitespace]
      test - '\r'.assertRefine[Whitespace]
      test - '\u001C'.assertRefine[Whitespace]
      test - '\u001D'.assertRefine[Whitespace]
      test - '\u001E'.assertRefine[Whitespace]
      test - '\u001F'.assertRefine[Whitespace]
      test - 'a'.assertNotRefine[Whitespace]

    test("lowercase"):
      test - 'a'.assertRefine[LowerCase]
      test - 'A'.assertNotRefine[LowerCase]
      test - ' '.assertNotRefine[LowerCase]
      test - '1'.assertNotRefine[LowerCase]

    test("uppercase"):
      test - 'A'.assertRefine[UpperCase]
      test - 'a'.assertNotRefine[UpperCase]
      test - ' '.assertNotRefine[UpperCase]
      test - '1'.assertNotRefine[UpperCase]

    test("digit"):
      test - '1'.assertRefine[Digit]
      test - 'a'.assertNotRefine[Digit]
      test - 'A'.assertNotRefine[Digit]
      test - '-'.assertNotRefine[Digit]

    test("letter"):
      test - 'a'.assertRefine[Letter]
      test - 'A'.assertRefine[Letter]
      test - '1'.assertNotRefine[Letter]
      test - '-'.assertNotRefine[Letter]

    test("special"):
      test - ' '.assertRefine[Special]
      test - '%'.assertRefine[Special]
      test - 'a'.assertNotRefine[Special]
      test - 'A'.assertNotRefine[Special]
      test - '1'.assertNotRefine[Special]
  }
