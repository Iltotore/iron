package io.github.iltotore.iron

trait Constraint[A, C]:

  inline def test(value: A): Boolean

  inline def message: String
