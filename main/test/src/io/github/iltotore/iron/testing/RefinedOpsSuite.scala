package io.github.iltotore.iron.testing

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.*
import utest.*

import scala.compiletime.summonInline
import scala.util.{Failure, Success, Try}

object RefinedTypeOpsSuite extends TestSuite:
  val tests: Tests = Tests {
    test("compile-time apply") {
      val temperature = Temperature(100)
      compileError("Temperature(-100)")
    }

    test("value") {
      val t1 = Temperature(100)
      val t2 = Temperature(100)

      val result = t1.value + t2.value
      assert(result == 200.0)
    }

    test("re-eval") {
      val x: Double :| Positive = 5.0
      val y: Double :| Greater[10] = 15.0
      val t1 = Temperature(x)
      val t2 = Temperature(y)

      assert(t1 == Temperature(5.0))
      assert(t2 == Temperature(15.0))
    }

    test("either") {
      val eitherWithFailingPredicate = Temperature.either(-5.0)
      assert(eitherWithFailingPredicate == Left("Should be strictly positive"))
      val eitherWithSucceedingPredicate = Temperature.either(100)
      assert(eitherWithSucceedingPredicate == Right(Temperature(100)))
    }

    test("option") {
      val fromWithFailingPredicate = Temperature.option(-5.0)
      assert(fromWithFailingPredicate == None)
      val fromWithSucceedingPredicate = Temperature.option(100)
      assert(fromWithSucceedingPredicate == Some(Temperature(100)))
    }

    test("applyUnsafe") {
      test - assertMatch(Try(Temperature.applyUnsafe(-100))) { case Failure(e) if e.getMessage == "Should be strictly positive" => }
      test - assert(Temperature.applyUnsafe(100) == Temperature(100))
    }

    test("assume") - assert(Temperature.assume(-15) == -15.0.asInstanceOf[Temperature])

    test("nonOpaque") {
      val moisture = Moisture(11)
      val positive: Double :| Positive = 11
      val greaterThan10: Double :| Greater[10] = 11

      test - assert(Moisture(positive) == moisture)
      test - assert(Moisture(greaterThan10) == moisture)
      test - assert(Moisture(positive) == moisture)
      test - assert(Moisture.either(-5.0) == Left("Should be strictly positive"))
      test - assert(Moisture.either(100) == Right(Moisture(100)))
      test - assert(Moisture.option(-5.0) == None)
      test - assert(Moisture.option(100) == Some(Moisture(100)))
    }

    test("mirror") {
      val mirror = summonInline[RefinedTypeOps.Mirror[Temperature]]

      assertGiven[mirror.BaseType =:= Double]
      assertGiven[mirror.ConstraintType =:= Positive]
      assertGiven[mirror.FinalType =:= Temperature]
    }
  }
