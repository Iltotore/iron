package io.github.iltotore.iron.testing

import io.github.iltotore.iron.{:|, IronType, autoRefine}
import io.github.iltotore.iron.constraint.numeric.*
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
  }