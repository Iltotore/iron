package io.github.iltotore.iron.testing

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.collection.*
import io.github.iltotore.iron.constraint.numeric.*
import utest.*

object CollectionSuite extends TestSuite:

  final class IsA

  given Constraint[Char, IsA] with

    override inline def test(inline value: Char): Boolean = value == 'a'

    override inline def message: String = "Should be 'a'"

  val tests: Tests = Tests {

    test("length"):
      test("iterable"):
        test - List(1, 2, 3, 4).assertRefine[Length[Greater[3]]]
        test - List(1, 2, 3).assertNotRefine[Length[Greater[3]]]

      test("string"):
        test - "1234".assertRefine[Length[Greater[3]]]
        test - "123".assertNotRefine[Length[Greater[3]]]

    test("minLength"):
      test("iterable"):
        test - List(1, 2, 3, 4).assertRefine[MinLength[4]]
        test - List(1, 2, 3).assertNotRefine[MinLength[4]]

      test("string"):
        test - "abc".assertNotRefine[MinLength[4]]
        test - "abcd".assertRefine[MinLength[4]]

    test("maxLength"):
      test("iterable"):
        test - List(1, 2, 3).assertRefine[MaxLength[3]]
        test - List(1, 2, 3, 4).assertNotRefine[MaxLength[3]]

      test("string"):
        test - "abc".assertRefine[MaxLength[3]]
        test - "abcd".assertNotRefine[MaxLength[3]]

    test("empty"):
      test("iterable"):
        test - Nil.assertRefine[Empty]
        test - List(1, 2, 3).assertNotRefine[Empty]

      test("string"):
        test - "".assertRefine[Empty]
        test - "abc".assertNotRefine[Empty]

    test("fixedLength"):
      test("iterable"):
        test - List(1, 2, 3).assertRefine[FixedLength[3]]
        test - List(1, 2).assertNotRefine[FixedLength[3]]
        test - List(1, 2, 3, 4).assertNotRefine[FixedLength[3]]

      test("string"):
        test - "abc".assertRefine[FixedLength[3]]
        test - "ab".assertNotRefine[FixedLength[3]]
        test - "abcd".assertNotRefine[FixedLength[3]]

    test("contain"):
      test("iterable"):
        test - List(1, 2, 3).assertRefine[Contain[3]]
        test - List(1, 2, 4).assertNotRefine[Contain[3]]

      test("string"):
        test - "abc".assertRefine[Contain["c"]]
        test - "abd".assertNotRefine[Contain["c"]]

    test("forAll"):
      test("iterable"):
        test - Nil.assertRefine[ForAll[IsA]]
        test - List('a', 'a', 'a').assertRefine[ForAll[IsA]]
        test - List('a', 'b', 'c').assertNotRefine[ForAll[IsA]]

      test("string"):
        test - "".assertRefine[ForAll[IsA]]
        test - "aaa".assertRefine[ForAll[IsA]]
        test - "abc".assertNotRefine[ForAll[IsA]]

    test("init"):
      test("iterable"):
        test - Nil.assertRefine[Init[IsA]]
        test - List('b').assertRefine[Init[IsA]]
        test - List('a', 'a', 'b').assertRefine[Init[IsA]]
        test - List('a', 'a', 'a').assertRefine[Init[IsA]]
        test - List('a', 'b', 'c').assertNotRefine[Init[IsA]]

      test("string"):
        test - "".assertRefine[Init[IsA]]
        test - "b".assertRefine[Init[IsA]]
        test - "aab".assertRefine[Init[IsA]]
        test - "aaa".assertRefine[Init[IsA]]
        test - "abc".assertNotRefine[Init[IsA]]

    test("tail"):
      test("iterable"):
        test - Nil.assertRefine[Tail[IsA]]
        test - List('b').assertRefine[Tail[IsA]]
        test - List('b', 'a', 'a').assertRefine[Tail[IsA]]
        test - List('a', 'a', 'a').assertRefine[Tail[IsA]]
        test - List('a', 'b', 'c').assertNotRefine[Tail[IsA]]

      test("string"):
        test - "".assertRefine[Tail[IsA]]
        test - "b".assertRefine[Tail[IsA]]
        test - "baa".assertRefine[Tail[IsA]]
        test - "aaa".assertRefine[Tail[IsA]]
        test - "abc".assertNotRefine[Tail[IsA]]

    test("exists"):
      test("iterable"):
        test - List('a', 'a', 'a').assertRefine[Exists[IsA]]
        test - List('a', 'b', 'c').assertRefine[Exists[IsA]]
        test - List('b', 'b', 'c').assertNotRefine[Exists[IsA]]
        test - Nil.assertNotRefine[Exists[IsA]]

      test("string"):
        test - "aaa".assertRefine[Exists[IsA]]
        test - "abc".assertRefine[Exists[IsA]]
        test - "bbc".assertNotRefine[Exists[IsA]]
        test - "".assertNotRefine[Exists[IsA]]

    test("head"):
      test("iterable"):
        test - List('a', 'b', 'c').assertRefine[Head[IsA]]
        test - List('c', 'b', 'a').assertNotRefine[Head[IsA]]
        test - List('b', 'b', 'c').assertNotRefine[Head[IsA]]
        test - Nil.assertNotRefine[Head[IsA]]

      test("string"):
        test - "abc".assertRefine[Head[IsA]]
        test - "cba".assertNotRefine[Head[IsA]]
        test - "bbc".assertNotRefine[Head[IsA]]
        test - "".assertNotRefine[Head[IsA]]

    test("last"):
      test("iterable"):
        test - List('c', 'b', 'a').assertRefine[Last[IsA]]
        test - List('a', 'b', 'c').assertNotRefine[Last[IsA]]
        test - List('b', 'b', 'c').assertNotRefine[Last[IsA]]
        test - Nil.assertNotRefine[Last[IsA]]

      test("string"):
        test - "cba".assertRefine[Last[IsA]]
        test - "abc".assertNotRefine[Last[IsA]]
        test - "bbc".assertNotRefine[Last[IsA]]
        test - "".assertNotRefine[Last[IsA]]
  }
