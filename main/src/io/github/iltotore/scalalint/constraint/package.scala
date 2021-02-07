package io.github.iltotore.scalalint

import scala.language.implicitConversions

package object constraint {
  
  implicit def valueToConstrained[T, C <: ConstraintAnchor](value: T)(using Constraint[T, C]): Constrained[T, C] =
    new Constrained[T, C](value)
  
  implicit def constrainedToValue[T](constrained: Constrained[T, _ <: ConstraintAnchor]): T = constrained.value
}