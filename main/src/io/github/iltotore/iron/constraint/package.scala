package io.github.iltotore.iron

import io.github.iltotore.iron._
import scala.compiletime._
import scala.language.implicitConversions
import math.Ordering.Implicits.infixOrderingOps

package object constraint {

  /**
   * Implicit assertion check
   * @param value the value passed to the assertion
   * @param constraint the applied type constraint
   * @tparam A the input type
   * @tparam B the constraint's dummy
   * @return the value as Constrained (meaning "asserted value")
   */
  implicit inline def valueToConstrained[A, B](value: A)(using inline constraint: Constraint[A, B]): Constrained[A, B] = {
    compileTime.preAssert(constraint.assert(value))
    Constrained(value)
  }

  /**
   * Implicit conversion from Constrained[A, B] to its shadowed type
   * @param constrained the Constrained to be cast from
   * @tparam A the input type
   * @tparam B the constraint's dummy
   * @return constrained as A
   */
  implicit inline def constrainedToValue[A, B](constrained: Constrained[A, B]): A = constrained
}
