package io.github.iltotore.iron.numeric

import io.github.iltotore.iron./
import io.github.iltotore.iron.constraint.*

import scala.compiletime.{constValue, summonInline}

object constraint {

  trait MathAlgebra

  /**
   * Constraint: checks if the input value is less than V.
   *
   * @tparam V
   */
  trait Less[V] extends AlgebraEntryPoint[MathAlgebra] with Transitive[V] with Asymmetric[V, Greater]

  type <[A, B] = BiOperator[A, B, MathAlgebra, Number, Less, Greater]

  class LessConstraint[A <: Number, V <: A] extends Constraint[A, Less[V]] {
    override inline def assert(value: A): Boolean = NumberOrdering.lt(value, constValue[V])

    override inline def getMessage(value: A): String = "value should be less than the specified one"
  }

  inline given[A <: Number, V <: A]: LessConstraint[A, V] = new LessConstraint

  /**
   * Constraint: checks if the input value is less or equal to V.
   *
   * @tparam V
   */
  trait LessEqual[V] extends AlgebraEntryPoint[MathAlgebra] with Order[V, GreaterEqual] with Or[Less[V], StrictEqual[V]]

  type <=[A, B] = BiOperator[A, B, MathAlgebra, Number, LessEqual, GreaterEqual]

  class LessEqualConstraint[A <: Number, V <: A] extends Constraint[A, LessEqual[V]] {
    override inline def assert(value: A): Boolean = NumberOrdering.lteq(value, constValue[V])

    override inline def getMessage(value: A): String = "value should be less or equal to the specified one"
  }

  inline given[A <: Number, V <: A]: LessEqualConstraint[A, V] = new LessEqualConstraint


  /**
   * Constraint: checks if the input value is greater than V.
   *
   * @tparam V
   */
  trait Greater[V] extends AlgebraEntryPoint[MathAlgebra] with Transitive[V] with Asymmetric[V, Less]

  type >[A, B] = BiOperator[A, B, MathAlgebra, Number, Greater, Less]

  /**
   * Alias for `T > 0`. Supports all non-floating primitives.
   *
   * @tparam T the primitive's type.
   */
  type Natural1[T] = T / (T match {
    case Byte => Greater[0]
    case Short => Greater[0]
    case Int => Greater[0]
    case Long => Greater[0L]
  })

  class GreaterConstraint[A <: Number, V <: A] extends Constraint[A, Greater[V]] {
    override inline def assert(value: A): Boolean = NumberOrdering.gt(value, constValue[V])

    override inline def getMessage(value: A): String = "value should be greater than the specified one"
  }

  inline given[A <: Number, V <: A]: GreaterConstraint[A, V] = new GreaterConstraint


  /**
   * Constraint: checks if the input value is greater or equal to V.
   *
   * @tparam V
   */
  trait GreaterEqual[V] extends AlgebraEntryPoint[MathAlgebra] with Order[V, LessEqual] with Or[Less[V], StrictEqual[V]]

  type >=[A, B] = BiOperator[A, B, MathAlgebra, Number, GreaterEqual, LessEqual]

  /**
   * Alias for `T >= 0`. Supports all non-floating primitives.
   *
   * @tparam T the primitive's type.
   */
  type Natural[T] = T / (T match {
    case Byte => GreaterEqual[0]
    case Short => GreaterEqual[0]
    case Int => GreaterEqual[0]
    case Long => GreaterEqual[0L]
  })

  class GreaterEqualConstraint[A <: Number, V <: A] extends Constraint[A, GreaterEqual[V]] {
    override inline def assert(value: A): Boolean = NumberOrdering.gteq(value, constValue[V])

    override inline def getMessage(value: A): String = "value should be greater or equal to the specified value"
  }

  inline given [A <: Number, V <: A]: GreaterEqualConstraint[A, V] = new GreaterEqualConstraint

  /**
   * Constraint: checks if the input value is divisible by V
   *
   * @tparam V
   */
  trait Divisible[V] extends Order[V, Divide]

  type %[A, B] = A / Divisible[B]

  /**
   * Abstraction over Divisible[2]. Supports all Number subtypes.
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


  /**
   * Constraint: checks if the input value divides V
   *
   * @tparam V
   */
  trait Divide[V] extends Order[V, Divisible]

  class DivideConstraint[A <: Number, V <: A] extends Constraint[A, Divide[V]] {

    override inline def assert(value: A): Boolean = modulo(constValue[V], value) == 0

    override inline def getMessage(value: A): String = "value should divide the specified value"
  }

  transparent inline given[A <: Number, V <: A]: Constraint[A, Divide[V]] = new DivideConstraint
}