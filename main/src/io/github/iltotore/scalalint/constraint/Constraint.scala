package io.github.iltotore.scalalint.constraint

trait Constraint[T, C <: ConstraintAnchor] {

  def assert(value: T): Unit
}