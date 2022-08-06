package io.github.iltotore.iron

trait Constraint[T, C]:

  inline def test(value: T): Boolean

  transparent inline def message: String
