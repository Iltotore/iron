package io.github.iltotore.iron.testing

import io.github.iltotore.iron.{:|, IronType, autoRefine}
import io.github.iltotore.iron.constraint.numeric.{Positive, *}
import utest.{TestSuite, Tests, compileError, test}
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import scala.util.{Failure, Try}

opaque type Temperature = Double :| Positive
object Temperature extends RefinedTypeOps[Temperature]

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
      val t1 = Temperature.fromIronType(x)
      val t2 = Temperature.fromIronType(y)

      assert(t1 == Temperature(5.0), "should be result of 'apply'")
      assert(t2 == Temperature(15.0), "should be result of 'apply'")
    }

    test("either") {
      val eitherWithFailingPredicate = Temperature.either(-5.0)
      assert(eitherWithFailingPredicate == Left("Should be strictly positive"), "'either' returns left if predicate fails")
      val eitherWithSucceedingPredicate = Temperature.either(100)
      assert(eitherWithSucceedingPredicate == Right(Temperature(100)), "right should contain result of 'apply'")
    }

    test("option") {
      val fromWithFailingPredicate = Temperature.option(-5.0)
      assert(fromWithFailingPredicate == None, "'option' returns None if predicate fails")
      val fromWithSucceedingPredicate = Temperature.option(100)
      assert(fromWithSucceedingPredicate == Some(Temperature(100)), "Some should contain result of 'apply'")
    }

    test("applyUnsafe") {
      val appliedUnsafeWithFailingPredicate = Try(Temperature.applyUnsafe(-1))

      assert(appliedUnsafeWithFailingPredicate.isFailure, "appliedUnsafe should fail when predicate fails")
      assert(
        appliedUnsafeWithFailingPredicate.toEither.swap.toOption.get.getMessage == "Should be strictly positive",
        "exception should duplicate message of failed predicate"
      )

      val appliedUnsafeWithSucceedingPredicate = Try(Temperature.applyUnsafe(100))
      assert(
        appliedUnsafeWithSucceedingPredicate.toOption.get == Temperature(100),
        "should be wrapped result of apply"
      )
    }

    test("assume") {
      val x: Double = -15.0
      val t1 = Temperature.assume(x)

      assert(t1 == -15.0.asInstanceOf[Temperature])
    }

    test("ops are being applied to non-opaque types and don't change behaviour") {
      type Moisture = Double :| Positive
      object Moisture extends RefinedTypeOps[Moisture]
      val moisture = Moisture(11)
      val positive: Double :| Positive = 11
      val greaterThan10: Double :| Greater[10] = 11

      assert(Moisture.fromIronType(positive) == moisture)
      assert(Moisture.fromIronType(greaterThan10) == moisture)
      assert(Moisture.fromIronType(positive) == moisture)
      val eitherWithFailingPredicate = Moisture.either(-5.0)
      assert(eitherWithFailingPredicate == Left("Should be strictly positive"), "'either' returns left if predicate fails")
      val eitherWithSucceedingPredicate = Moisture.either(100)
      assert(eitherWithSucceedingPredicate == Right(Moisture(100)), "right should contain result of 'apply'")
      val fromWithFailingPredicate = Moisture.option(-5.0)
      assert(fromWithFailingPredicate == None, "'option' returns None if predicate fails")
      val fromWithSucceedingPredicate = Moisture.option(100)
      assert(fromWithSucceedingPredicate == Some(Moisture(100)), "Some should contain result of 'apply'")
    }

  }
