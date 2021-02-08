package io.github.iltotore.scalalint

import scala.language.implicitConversions

package object constraint {

  implicit def valueToConstrained[T, C <: ConstraintAnchor](value: T)(using Constraint[T, C]): Constrained[T, C] =
    new Constrained[T, C](value)

  implicit def constrainedToValue[T](constrained: Constrained[T, _ <: ConstraintAnchor]): T = constrained.value

  def composedConstraint[T, C <: ConstraintAnchor, D <: ConstraintAnchor](using first: Constraint[T, C], second: Constraint[T, D]): Constraint[T, C & D] = new Constraint[T, C & D] {

    override def assert(value: T): Option[String] = first.assert(value).orElse(second.assert(value))
  }
}