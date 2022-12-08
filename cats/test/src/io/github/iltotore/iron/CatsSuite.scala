package io.github.iltotore.iron

import _root_.cats.Show
import _root_.cats.kernel.*
import _root_.cats.derived.*
import _root_.cats.instances.all.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.given
import io.github.iltotore.iron.constraint.all.*

import utest.{Show as _, *}

object CatsSuite extends TestSuite:

  type AgeR = DescribedAs[
    Greater[0] & Less[120],
    "Persons's age must be an integer between 1 and 120"
  ]

  type NameR = DescribedAs[
    Alphanumeric & MinLength[1] & MaxLength[50],
    "Person's name must be an alphanumeric of max length 50"
  ]

  case class Person(
      name: String :| NameR,
      surname: String :| Contain["z"],
      age: Int :| Greater[0]
  ) derives Eq, Monoid, Order, Show

  given BoundedSemilattice[String] = BoundedSemilattice.instance("", _ + _)

  val tests: Tests = Tests {

    test("Cats instances are resolved for String iron types") {
      Band[String :| NameR]
      BoundedSemilattice[String :| NameR]
      CommutativeMonoid[String :| NameR]
      CommutativeSemigroup[String :| NameR]
      Eq[String :| NameR]
      Hash[String :| NameR]
      LowerBounded[String :| NameR]
      Monoid[String :| NameR]
      Order[String :| NameR]
      PartialOrder[String :| NameR]
      Semigroup[String :| NameR]
      Semilattice[String :| NameR]
      Show[String :| NameR]
      compileError("UpperBounded[String :| NameR]")
      compileError("CommutativeGroup[String :| NameR]")
      ()
    }

    test("Cats instances are resolved for Int iron types") {
      CommutativeGroup[Int :| AgeR]
      CommutativeMonoid[Int :| AgeR]
      CommutativeSemigroup[Int :| AgeR]
      Eq[Int :| AgeR]
      Group[Int :| AgeR]
      Hash[Int :| AgeR]
      UpperBounded[Int :| AgeR]
      Monoid[Int :| AgeR]
      Order[Int :| AgeR]
      PartialOrder[Int :| AgeR]
      Semigroup[Int :| AgeR]
      Show[Int :| AgeR]
      LowerBounded[Int :| AgeR]
      ()
    }

    test("Cats instances are resolved for a case class with iron types") {
      Eq[Person]
      Order[Person]
      Monoid[Person]
      Semigroup[Person]
      Show[Person]
      ()
    }
  }
