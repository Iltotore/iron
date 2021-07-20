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

  /**
   * Generate the assertion message using `value`.
   * @param value the input value
   * @return the message describing this Constraint
   * @note For compile-time constraints, you should use only litteral strings to allow the macro to get the value at
   *       compile-time (and print it correctly when a compile-time error occurs).
   */
  inline def getMessage(value: A): String
}

object Constraint {

  trait RuntimeOnly[A, B] extends Constraint[A, B]

  trait CompileTimeOnly[A, B] extends Constraint[A, B]
}