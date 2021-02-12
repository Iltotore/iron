package io.github.iltotore.scalalint.constraint

class Inlined[T, C <: ConstraintAnchor](val value: T)(using constraint: Constraint[T, C])