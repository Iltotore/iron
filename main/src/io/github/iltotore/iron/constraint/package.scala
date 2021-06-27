package io.github.iltotore.iron

import io.github.iltotore.iron.{Constrained, compileTime}
import scala.language.implicitConversions
import math.Ordering.Implicits.infixOrderingOps

package object constraint {

  /**
   * Implicit assertion check
   *
   * @param value      the value passed to the assertion
   * @param constraint the applied type constraint
   * @tparam A the input type
   * @tparam B the constraint's dummy
   * @return the value as Constrained (meaning "asserted value")
   * @note Due to a type inference bug of Scala 3, [[constrainedToValue]] was moved to the package object.
   */
  implicit inline def valueToConstrained[A, B](value: A)(using inline constraint: Constraint[A, B]): Constrained[A, B] = {
    Constrained(compileTime.preAssert(value, constraint))
  }

  //Due to a Dotty type inference bug,
}
