package io.github.iltotore.scalalint.constraint

trait InlinedConstraint[T, C <: ConstraintAnchor] extends Constraint[T, C] {

  override def assert(value: T): Option[String] = throw NotInlinedError("value")
  
  inline def assertInlined(inline value: T): Option[String]
}
