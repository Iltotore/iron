package io.github.iltotore.iron.testing

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import utest.*

object CharSuite extends TestSuite:

  val tests: Tests = Tests {

    test("blank") { //See https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Character.html#isWhitespace(char)
      test - ' '.assertRefine[Blank]
      test - '\t'.assertRefine[Blank]
      test - '\n'.assertRefine[Blank]
      test - '\u000B'.assertRefine[Blank]
      test - '\f'.assertRefine[Blank]
      test - '\r'.assertRefine[Blank]
      test - '\u001C'.assertRefine[Blank]
      test - '\u001D'.assertRefine[Blank]
      test - '\u001E'.assertRefine[Blank]
      test - '\u001F'.assertRefine[Blank]
      test - 'a'.assertNotRefine[Blank]
    }

    test("lowercase") {
      test - 'a'.assertRefine[LowerCase]
      test - 'A'.assertNotRefine[LowerCase]
      test - ' '.assertNotRefine[LowerCase]
      test - '1'.assertNotRefine[LowerCase]
    }

    test("uppercase") {
      test - 'A'.assertRefine[UpperCase]
      test - 'a'.assertNotRefine[UpperCase]
      test - ' '.assertNotRefine[UpperCase]
      test - '1'.assertNotRefine[UpperCase]
    }

    test("digit") {
      test - '1'.assertRefine[Digit]
      test - 'a'.assertNotRefine[Digit]
      test - 'A'.assertNotRefine[Digit]
      test - '-'.assertNotRefine[Digit]
    }

    test("letter") {
      test - 'a'.assertRefine[Letter]
      test - 'A'.assertRefine[Letter]
      test - '1'.assertNotRefine[Letter]
      test - '-'.assertNotRefine[Letter]
    }

    test("special") {
      test - ' '.assertRefine[Special]
      test - '%'.assertRefine[Special]
      test - 'a'.assertNotRefine[Special]
      test - 'A'.assertNotRefine[Special]
      test - '1'.assertNotRefine[Special]
    }
  }