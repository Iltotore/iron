package io.github.iltotore.scalalint

import scala.language.implicitConversions

package object constraint {

  implicit def valueToConstrained[T, C <: ConstraintAnchor](value: T)(using Constraint[T, C]): Constrained[T, C] = Constrained[T, C](value)

  implicit def constrainedToValue[T](constrained: Constrained[T, _ <: ConstraintAnchor]): T = constrained.value

  implicit inline def valueToInlined[T, C <: ConstraintAnchor](inline value: T)(using c: Constraint[T, C]): Inlined[T, C] = {

    inline c match {

      case inlined: InlinedConstraint[T, C] => inlined.assertInlined(value)

      case other: Constraint[T, C] => other.assert(value)
    }
    
    Inlined[T, C](value)
  }

  implicit inline def inlineToValue[T](inlined: Inlined[T, _ <: ConstraintAnchor]): T = inlined.value

  def composedConstraint[T, C <: ConstraintAnchor, D <: ConstraintAnchor](using first: Constraint[T, C], second: Constraint[T, D]): Constraint[T, C & D] = new Constraint[T, C & D] {

    override def assert(value: T): Option[String] = first.assert(value).orElse(second.assert(value))
  }

  def composedInlinedConstraint[T, C <: ConstraintAnchor, D <: ConstraintAnchor](using first: Constraint[T, C], second: Constraint[T, D]): Constraint[T, C & D] = new InlinedConstraint[T, C & D] {

    override inline def assertInlined(inline value: T): Option[String] = {
      first match {

        case inlined: InlinedConstraint[T, C] => inlined.assertInlined(value)

        case _ => first.assert(value)
      } orElse(second match {

        case inlined: InlinedConstraint[T, C] => inlined.assertInlined(value)

        case _ => second.assert(value)
      })
    }
  }
}