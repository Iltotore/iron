package io.github.iltotore.iron.constraint

import io.github.iltotore.iron.constraint.any.{*, given}
import io.github.iltotore.iron.ops.*
import io.github.iltotore.iron.ordering.NumberOrdering
import io.github.iltotore.iron.{==>, Constraint, Implication, IntNumber, Number}

import scala.compiletime.constValue
import scala.compiletime.ops.any.ToString

/**
 * Number-related constraints.
 */
object numeric:

  /**
   * Tests strict superiority.
   *
   * @tparam V the value the input must be greater than.
   */
  final class Greater[V]

  inline given [A <: Number, V <: A]: Constraint[A, Greater[V]] with

    override inline def test(value: A): Boolean = NumberOrdering.gt(value, constValue[V])

    override inline def message: String = "Should be greater than " + stringValue[V]

  inline given [V1, V2](using V1 > V2 =:= true): (Greater[V1] ==> Greater[V2]) = Implication()
  inline given [V1, V2](using V1 > V2 =:= true): (StrictEqual[V1] ==> Greater[V2]) = Implication()

  /**
   * Tests non-strict superiority.
   *
   * @tparam V the value the input must be greater than or equal to.
   */
  type GreaterEqual[V] = (Greater[V] || StrictEqual[V]) DescribedAs ("Should be greater than or equal to " + V)

  /**
   * Tests strict inferiority.
   *
   * @tparam V the value the input must be less than.
   */
  final class Less[V]

  inline given [A <: Number, V <: A]: Constraint[A, Less[V]] with

    override inline def test(value: A): Boolean = NumberOrdering.lt(value, constValue[V])

    override inline def message: String = "Should be less than " + stringValue[V]

  inline given [V1, V2](using V1 < V2 =:= true): (Less[V1] ==> Less[V2]) = Implication()

  inline given [V1, V2](using V1 < V2 =:= true): (StrictEqual[V1] ==> Less[V2]) = Implication()

  /**
   * Tests non-strict inferiority.
   *
   * @tparam V the value the input must be less than or equal to.
   */
  type LessEqual[V] = (Less[V] || StrictEqual[V]) DescribedAs ("Should be less than or equal to " + V)

  /**
   * Tests if the input is a multiple of V.
   *
   * @tparam V the expected divisor of the given input.
   * @see [[Divide]]
   */
  final class Multiple[V]

  inline given [A <: IntNumber, V <: A]: Constraint[A, Multiple[V]] with

    override inline def test(value: A): Boolean = modulo(value, constValue[V]) == 0

    override inline def message: String = "Should be a multiple of " + stringValue[V]

  given [A, V1 <: A, V2 <: A](using V1 % V2 =:= Zero[A]): (Multiple[V1] ==> Multiple[V2]) = Implication()

  /**
   * Tests if the input is a divisor of V.
   *
   * @tparam V the expected multiple of the given input.
   * @see [[Multiple]]
   */
  final class Divide[V]

  inline given [A <: IntNumber, V <: A]: Constraint[A, Divide[V]] with

    override inline def test(value: A): Boolean = modulo(constValue[V], value) == 0

    override inline def message: String = "Should divide " + stringValue[V]
