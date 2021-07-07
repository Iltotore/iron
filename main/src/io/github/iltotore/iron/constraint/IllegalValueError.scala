package io.github.iltotore.iron.constraint

/**
 * Represents a type-level assertion failure.
 * @param input the invalid value
 * @param constraint the type constraint applied to [[input]]
 * @tparam A the input type
 */
case class IllegalValueError[A](input: A, constraint: Constraint[A, ?]) extends Error