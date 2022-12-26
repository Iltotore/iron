package io.github.iltotore.iron.testing

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.collection.*
import utest.*

object CollectionSuite extends TestSuite:

  final class IsA

  given Constraint[Char, IsA] with

    override inline def test(value: Char): Boolean = value == 'a'

    override inline def message: String = "Should be 'a'"

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

    test("forAll") {

      test("iterable") {
        test - Nil.assertRefine[ForAll[IsA]]
        test - List('a', 'a', 'a').assertRefine[ForAll[IsA]]
        test - List('a', 'b', 'c').assertNotRefine[ForAll[IsA]]
      }

      test("string") {
        test - "".assertRefine[ForAll[IsA]]
        test - "aaa".assertRefine[ForAll[IsA]]
        test - "abc".assertNotRefine[ForAll[IsA]]
      }
    }
  }
