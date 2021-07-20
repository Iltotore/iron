package io.github.iltotore.iron.constraint

/**
 * Represents the constraint associated to an input value and a dummy type.
 * @tparam A the input type
 * @tparam B the constraint's dummy
 */
trait Constraint[A, B] {

  /**
   * Asserts the given value.
   * @param value the value to be checked
   * @return true if the assertion is passed, false otherwise
   */
  inline def assert(value: A): Boolean

  /**
   * A dummy method for [[assert]] to allow non-inline calls.
   * @param value the value to be checked
   * @return true if the assertion is passed, false otherwise
   */
  inline def runtimeAssert(value: A): Boolean = assert(value)

  inline def getMessage(value: A): String
}

object Constraint {

  trait RuntimeOnly[A, B] extends Constraint[A, B]

  trait CompileTimeOnly[A, B] extends Constraint[A, B]
}