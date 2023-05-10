package io.github.iltotore.iron.testing

import io.github.iltotore.iron.{:|, IronType, autoRefine}
import io.github.iltotore.iron.constraint.numeric.*
import utest.{TestSuite, Tests, test}
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.{given, *}

class RefinedTypeOps[A, C]

extension [A, C](ops: RefinedTypeOps[A, IronType[A, C]])
  inline def either(a: A)(using inline constraint: Constraint[A, C]): Either[String, A :| C] = a.refineEither[C]
  inline def apply(inline value: A)(using inline constraint: Constraint[A, C]): A :| C =
    autoRefine(value)

type Temperature = Double :| Positive
object Temperature extends RefinedTypeOps[Double, Temperature]

type Moisture = Double :| Positive
object Moisture extends RefinedTypeOps[Double, Moisture]

object RefinedOpsSuite extends TestSuite:
  val tests: Tests = Tests {
    test("temperature") {
      val result: Either[String, Temperature] = Temperature.either(2.0)
      val t: Temperature = 2
    }
  }

/*
opaque type Temperature = Double :| Positive
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

value apply is not a member of object io.github.iltotore.iron.testing.Temperature.
Extension methods were tried, but could not be fully constructed:

    io.github.iltotore.iron.autoRefine[
      io.github.iltotore.iron.testing.Temperature.type
    , C](io.github.iltotore.iron.testing.Temperature)(
      io.github.iltotore.iron.Constraint.given_UnionConstraint_A_C[
        io.github.iltotore.iron.testing.Temperature.type
      , C](<empty>)
    )

    io.github.iltotore.iron.testing.apply[A, C](Temperature)    failed with

        Found:    io.github.iltotore.iron.testing.Temperature.type
        Required: io.github.iltotore.iron.testing.RefinedTypeOps[A,
          io.github.iltotore.iron.IronType[A, C]
        ]

        where:    A is a type variable
                  C is a type variable

      val t = Temperature(2)


val t: Temperature = 2
^^^^^^^^^^^^^^^^^^^^^^

Found:    (2 : Int)
Required: io.github.iltotore.iron.testing.Temperature
      val t: Temperature = 2

 */
