package io.github.iltotore.iron.testing

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.*
import utest.*

import scala.compiletime.summonInline
import scala.util.{Failure, Success, Try}

object RefinedTypeSuite extends TestSuite:
  val tests: Tests = Tests {
    test("compile-time apply"):
      val temperature = Temperature(100)
      compileError("Temperature(-100)")

    test("value"):
      val t1 = Temperature(100)
      val t2 = Temperature(100)

      val result = t1.value + t2.value
      assert(result == 200.0)

    test("re-eval"):
      val x: Double :| Positive = 5.0
      val y: Double :| Greater[10] = 15.0
      val t1 = Temperature(x)
      val t2 = Temperature(y)

      assert(t1 == Temperature(5.0))
      assert(t2 == Temperature(15.0))

    test("assume") - assert(Temperature.assume(-15) == -15.0.asInstanceOf[Temperature])

    test("applyUnsafe"):
      test - assertMatch(Try(Temperature.applyUnsafe(-100))):
        case Failure(e) if e.getMessage == "Should be strictly positive" =>
      test - assert(Temperature.applyUnsafe(100) == Temperature(100))

    test("either"):
      val eitherWithFailingPredicate = Temperature.either(-5.0)
      assert(eitherWithFailingPredicate == Left("Should be strictly positive"))
      val eitherWithSucceedingPredicate = Temperature.either(100)
      assert(eitherWithSucceedingPredicate == Right(Temperature(100)))

    test("option"):
      val fromWithFailingPredicate = Temperature.option(-5.0)
      assert(fromWithFailingPredicate.isEmpty)
      val fromWithSucceedingPredicate = Temperature.option(100)
      assert(fromWithSucceedingPredicate.contains(Temperature(100)))

    test("assumeAll") - assert(Temperature.assumeAll(List(1, -15)) == List(1, -15).asInstanceOf[List[Temperature]])

    test("applyAllUnsafe"):
      test - assertMatch(Try(Temperature.applyAllUnsafe(List(1, 2, -3)))):
        case Failure(e) if e.getMessage == "Should be strictly positive" =>
      test - assert(Temperature.applyAllUnsafe(List(1, 2, 3)) == List(Temperature(1), Temperature(2), Temperature(3)))

    test("either"):
      test - assert(Temperature.eitherAll(List(1, 2, -3)) == Left("Should be strictly positive"))
      test - assert(Temperature.eitherAll(List(1, 2, 3)) == Right(List(Temperature(1), Temperature(2), Temperature(3))))

    test("option"):
      test - assert(Temperature.optionAll(List(1, 2, -3)).isEmpty)
      test - assert(Temperature.optionAll(List(1, 2, 3)).contains(List(Temperature(1), Temperature(2), Temperature(3))))

    test("mirror"):
      val mirror = summonInline[RefinedType.Mirror[Temperature]]

      assertGiven[mirror.BaseType =:= Double]
      assertGiven[mirror.ConstraintType =:= Positive]
      assertGiven[mirror.FinalType =:= Temperature]

    test("value"):
      val temperature = Temperature(10)
      assert(temperature.value == 10)

    test("extensions"):
      val temperature = Temperature(10)
      assert(temperature + Temperature(10) == Temperature(20))
      assert(temperature + 10 == Temperature(20))
      compileError("temperature - 10")

    test("unapply"):
      val temperature = Temperature(10)

      temperature match
        case Temperature(raw) => (raw: Double)
  }
