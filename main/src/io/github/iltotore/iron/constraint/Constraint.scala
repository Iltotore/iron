package io.github.iltotore.iron.constraint

trait Constraint[A, B] {

  inline def assert(value: A): Boolean
  
  inline def runtimeAssert(value: A): Boolean = assert(value)
}
