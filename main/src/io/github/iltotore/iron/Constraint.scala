package io.github.iltotore.iron

/**
 * A typeclass representing the implementation of a constraint of a certain type of value.
 *
 * @tparam A the type of the value to test.
 * @tparam C the constraint associated with this implementation.
 */
trait Constraint[A, C]:

  inline def test(value: A): Boolean

  inline def message: String
