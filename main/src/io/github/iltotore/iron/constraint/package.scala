package io.github.iltotore.iron

import io.github.iltotore.iron._
import scala.compiletime._
import scala.language.implicitConversions
import math.Ordering.Implicits.infixOrderingOps

package object constraint {

  implicit inline def valueToConstrained[A, B](value: A)(using inline constraint: Constraint[A, B]): Constrained[A, B] = {
    compileTime.preAssert(constraint.assert(value))
    Constrained.unchecked(value)
  }
  
}
