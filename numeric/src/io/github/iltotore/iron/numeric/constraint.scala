package io.github.iltotore.iron.numeric

import io.github.iltotore.iron.==>
import io.github.iltotore.iron.constraint.Constraint

import scala.compiletime.{constValue, summonInline}

object constraint {

  /**
   * Constraint: checks if the input value is less than V.
   *
   * @tparam V
   */
  trait Less[V]

  type <[A, B] = A ==> Less[B]

  class LessConstraint[A <: Number, V <: A] extends Constraint[A, Less[V]] {
    override inline def assert(value: A): Boolean = NumberOrdering.lt(value, constValue[V])

    override inline def getMessage(value: A): String = "value should be less than the specified value"
  }

  inline given[A <: Number, V <: A]: LessConstraint[A, V] = new LessConstraint


  /**
   * Constraint: checks if the input value is less or equal to V.
   *
   * @tparam V
   */
  trait LessEqual[V]

  type <=[A, B] = A ==> LessEqual[B]

  class LessEqualConstraint[A <: Number, V <: A] extends Constraint[A, LessEqual[V]] {
    override inline def assert(value: A): Boolean = NumberOrdering.lteq(value, constValue[V])

    override inline def getMessage(value: A): String = "value should be less or equal to the specified value"
  }

  inline given[A <: Number, V <: A]: LessEqualConstraint[A, V] = new LessEqualConstraint


  /**
   * Constraint: checks if the input value is greater than V.
   *
   * @tparam V
   */
  trait Greater[V]

  type >[A, B] = A ==> Greater[B]

  /**
   * Alias for `T > 0`. Supports all non-floating primitives.
   *
   * @tparam T the primitive's type.
   */
  type Natural1[T] = T ==> (T match {
    case Byte => Greater[0]
    case Short => Greater[0]
    case Int => Greater[0]
    case Long => Greater[0L]
  })

  class GreaterConstraint[A <: Number, V <: A] extends Constraint[A, Greater[V]] {
    override inline def assert(value: A): Boolean = NumberOrdering.gt(value, constValue[V])

    override inline def getMessage(value: A): String = "value should be greater than the specified value"
  }

  inline given[A <: Number, V <: A]: GreaterConstraint[A, V] = new GreaterConstraint


  /**
   * Constraint: checks if the input value is greater or equal to V.
   *
   * @tparam V
   */
  trait GreaterEqual[V]

  type >=[A, B] = A ==> GreaterEqual[B]

  /**
   * Alias for `T >= 0`. Supports all non-floating primitives.
   *
   * @tparam T the primitive's type.
   */
  type Natural[T] = T >= (T match {
    case Byte => GreaterEqual[0]
    case Short => GreaterEqual[0]
    case Int => GreaterEqual[0]
    case Long => GreaterEqual[0L]
  })

  class GreaterEqualConstraint[A <: Number, V <: A] extends Constraint[A, GreaterEqual[V]] {
    override inline def assert(value: A): Boolean = NumberOrdering.gteq(value, constValue[V])

    override inline def getMessage(value: A): String = "value should be greater or equal to the specified value"
  }

  inline given[A <: Number, V <: A]: GreaterEqualConstraint[A, V] = new GreaterEqualConstraint

  /**
   * Constraint: checks if the input value is divisible by V
   *
   * @tparam V
   */
  trait Divisible[V]

  type %[A, B] = A ==> Divisible[B]

  /**
   * Alias for `T % 2`. Supports all non-floating primitives.
   *
   * @tparam T the primitive's type.
   */
  type Even[T] = T match {
    case Byte => Divisible[2]
    case Short => Divisible[2]
    case Int => Divisible[2]
    case Long => Divisible[2L]
    case Float => Divisible[2F]
    case Double => Divisible[2D]
  }

  class DivisibleConstraint[A <: Number, V <: A] extends Constraint[A, Divisible[V]] {
    override inline def assert(value: A): Boolean = modulo(value, constValue[V]) == 0

    override inline def getMessage(value: A): String = "value should be divisible by the specified value"
  }

  inline given[A <: Number, V <: A]: DivisibleConstraint[A, V] = new DivisibleConstraint
}