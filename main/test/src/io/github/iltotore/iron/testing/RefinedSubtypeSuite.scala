package io.github.iltotore.iron.testing

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.*
import utest.*

import scala.compiletime.summonInline
import scala.util.{Failure, Success, Try}

object RefinedSubtypeSuite extends TestSuite:
  val tests: Tests = Tests {
    test("compile-time apply"):
      val altitude = Altitude(100)
      compileError("Height(-100)")

    test("value"):
      val a1 = Altitude(100)
      val h2 = Altitude(100)

      val result = a1.value + h2.value
      assert(result == 200.0)

    test("re-eval"):
      val x: Double :| Positive = 5.0
      val y: Double :| Greater[10] = 15.0
      val a1 = Altitude(x)
      val a2 = Altitude(y)

      assert(a1 == Altitude(5.0))
      assert(a2 == Altitude(15.0))

    test("assume") - assert(Altitude.assume(-15) == -15.0.asInstanceOf[Altitude])

    test("applyUnsafe"):
      test - assertMatch(Try(Altitude.applyUnsafe(-100))):
        case Failure(e) if e.getMessage == "Should be strictly positive" =>
      test - assert(Altitude.applyUnsafe(100) == Altitude(100))

    test("either"):
      val eitherWithFailingPredicate = Altitude.either(-5.0)
      assert(eitherWithFailingPredicate == Left("Should be strictly positive"))
      val eitherWithSucceedingPredicate = Altitude.either(100)
      assert(eitherWithSucceedingPredicate == Right(Altitude(100)))

    test("option"):
      val fromWithFailingPredicate = Altitude.option(-5.0)
      assert(fromWithFailingPredicate.isEmpty)
      val fromWithSucceedingPredicate = Altitude.option(100)
      assert(fromWithSucceedingPredicate.contains(Altitude(100)))

    test("assumeAll") - assert(Altitude.assumeAll(List(1, -15)) == List(1, -15).asInstanceOf[List[Altitude]])

    test("applyAllUnsafe"):
      test - assertMatch(Try(Altitude.applyAllUnsafe(List(1, 2, -3)))):
        case Failure(e) if e.getMessage == "Should be strictly positive" =>
      test - assert(Altitude.applyAllUnsafe(List(1, 2, 3)) == List(Altitude(1), Altitude(2), Altitude(3)))

    test("either"):
      test - assert(Altitude.eitherAll(List(1, 2, -3)) == Left("Should be strictly positive"))
      test - assert(Altitude.eitherAll(List(1, 2, 3)) == Right(List(Altitude(1), Altitude(2), Altitude(3))))

    test("option"):
      test - assert(Altitude.optionAll(List(1, 2, -3)).isEmpty)
      test - assert(Altitude.optionAll(List(1, 2, 3)).contains(List(Altitude(1), Altitude(2), Altitude(3))))

    test("mirror"):
      val mirror = summonInline[RefinedSubtype.Mirror[Altitude]]

      assertGiven[mirror.BaseType =:= Double]
      assertGiven[mirror.ConstraintType =:= Positive]
      assertGiven[mirror.FinalType =:= Altitude]

    test("value"):
      val altitude = Altitude(10)
      assert(altitude.value == 10)

    test("subtyping"):
      val altitude = Altitude(20)
      assert(altitude == 20)

    test("extensions"):
      val altitude = Altitude(10)
      assert(altitude + Altitude(10) == Altitude(20))
      assert(altitude + 10 == Altitude(20))
      altitude - 10 == 0
  }
