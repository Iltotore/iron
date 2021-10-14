package io.github.iltotore.iron

import io.github.iltotore.iron.{Constrained, compileTime}

import scala.language.implicitConversions
import scala.compiletime.{constValue, summonInline}
import scala.math.Ordering.Implicits.infixOrderingOps

package object constraint {

  /**
   * Implicit assertion check
   *
   * @param value      the value passed to the assertion
   * @param constraint the applied type constraint
   * @tparam A the input type
   * @tparam B the constraint's dummy
   * @return the value as Constrained (meaning "asserted value")
   * @note Due to a type inference bug of Scala 3, [[constrainedToValue]] was moved to the package object.
   */
  implicit inline def refineValue[A, B, C <: Constraint[A, B]](value: A)(using inline constraint: C): Constrained[A, B] = {
    Constrained(compileTime.preAssert(value, constraint))
  }

  /**
   * Represent a part of an algebraic expression.
   * @tparam T the algebra type to avoid clashes
   *
   */
  trait AlgebraPart[T, V]

  /**
   * Represent an entry point of the algebraic expression. Example: `??? < 1d` is an entry point.
   * @tparam T the algebra type to avoid clashes
   */
  trait AlgebraEntryPoint[T]

  /**
   * Alias for binary algebraic operator.
   * @tparam A the algebra part to the left of the operator
   * @tparam B the right input to the right of the operator
   * @tparam Alg the algebra type of this operator
   * @tparam Literal the type of the possible literals for this operator
   * @tparam Left the constraint type of this operator for the pattern `??? < B` or `A < B`
   * @tparam Right the constraint type of this operator for the pattern `A > ???`
   */
  type BiOperator[A, B, Alg, Literal, Left[_], Right[_]] = A match {
    case ?? => Left[B]
    case AlgebraEntryPoint[Alg] => AlgebraPartAnd[A, Left[B], Alg, B]
    case AlgebraPart[Alg, v] => AlgebraPartAnd[A, Placehold[Left[B], Alg, v], Alg, v]
    case Literal => B match {
      case ?? => Right[A]
      case _ => A / Left[B]
    }
    case _ => A / Left[B]
  }

  /**
   * Placeholder for algebraic expressions
   */
  final class ??

  class PlaceholderConstraint[A] extends Constraint[A, ??] {

    override inline def assert(value: A): Boolean = true

    override inline def getMessage(value: A): String = "True"
  }

  inline given [A]: PlaceholderConstraint[A] = new PlaceholderConstraint

  /**
   * Constraint: checks if the input value strictly equals to V.
   *
   * @tparam V
   */
  trait StrictEqual[V]

  type ==[A, V] = A / StrictEqual[V]

  class StrictEqualConstraint[A, V <: A] extends Constraint[A, StrictEqual[V]] {

    override inline def assert(value: A): Boolean = value == constValue[V]

    override inline def getMessage(value: A): String = s"$value should strictly equal to ${constValue[V]}"
  }

  inline given[A, V <: A]: StrictEqualConstraint[A, V] = new StrictEqualConstraint


  /**
   * Constraint: checks if the input value equals (using Any#equals) to V.
   *
   * @tparam V
   * @note This constraint is runtime-only
   */
  trait Equal[V]

  class EqualConstraint[A, V <: A] extends Constraint[A, Equal[V]] {

    override inline def assert(value: A): Boolean = value equals constValue[V]

    override inline def getMessage(value: A): String = s"$value should equal to ${constValue[V]}"
  }

  inline given[A, V <: A]: EqualConstraint[A, V] = new EqualConstraint


  /**
   * Constraint: checks if the input value doesn't pass B's constraint.
   *
   * @tparam B the reversed constraint's dummy
   */
  trait Not[B]

  type \[A, V] = A / Not[StrictEqual[V]]

  /**
   * Equivalent of the mathematical implication `=>`. Alias for `Not[A] || B`
   * @tparam A the antecedent of B
   * @tparam B the consequence of A
   */
  type ==>>[A, B] = Not[A] || B

  class NotConstraint[A, B, C <: Constraint[A, B]](using constraint: C) extends Constraint.RuntimeOnly[A, Not[B]] {

    override inline def assert(value: A): Boolean = !constraint.assert(value)

    override inline def getMessage(value: A): String = s"Not: ${constraint.getMessage(value)}"
  }

  inline given[A, B, C <: Constraint[A, B]](using C): NotConstraint[A, B, C] = new NotConstraint


  /**
   * Constraint: checks if the value pass B or C. Acts like a boolean OR.
   *
   * @tparam B the first constraint's dummy
   * @tparam C the second constraint's dummy
   */
  trait Or[B, C]

  type ||[B, C] = Or[B, C]

  class OrConstraint[A, B, C, CB <: Constraint[A, B], CC <: Constraint[A, C]](using left: CB, right: CC) extends Constraint.RuntimeOnly[A, Or[B, C]] {

    override inline def assert(value: A): Boolean = left.assert(value) || right.assert(value)

    override inline def getMessage(value: A): String = s"${left.getMessage(value)} or ${right.getMessage(value)}"
  }

  inline given[A, B, C, CB <: Constraint[A, B], CC <: Constraint[A, C]](using CB, CC): OrConstraint[A, B, C, CB, CC] = new OrConstraint


  /**
   * Constraint: checks if the value pass both B and C. Acts like a boolean AND.
   *
   * @tparam B the first constraint's dummy
   * @tparam C the second constraint's dummy
   */
  trait And[B, C]

  type &&[B, C] = And[B, C]

  class AndConstraint[A, B, C, CB <: Constraint[A, B], CC <: Constraint[A, C]](using left: CB, right: CC) extends Constraint.RuntimeOnly[A, And[B, C]] {

    override inline def assert(value: A): Boolean = left.assert(value) && right.assert(value)

    override inline def getMessage(value: A): String = s"${left.getMessage(value)} and ${right.getMessage(value)}"
  }

  inline given[A, B, C, CB <: Constraint[A, B], CC <: Constraint[A, C]](using CB, CC): AndConstraint[A, B, C, CB, CC] = new AndConstraint


  final class AlgebraPartAnd[B, C, Alg, V] extends AlgebraPart[Alg, V]

  class AlgebaricAndConstraint[A, B, C, CB <: Constraint[A, B], CC <: Constraint[A, C], Alg, V](using left: CB, right: CC) extends Constraint.RuntimeOnly[A, AlgebraPartAnd[B, C, Alg, V]] {

    override inline def assert(value: A): Boolean = left.assert(value) && right.assert(value)

    override inline def getMessage(value: A): String = s"${left.getMessage(value)} and ${right.getMessage(value)}"
  }

  inline given[A, B, C, CB <: Constraint[A, B], CC <: Constraint[A, C], Alg, V](using CB, CC): AlgebaricAndConstraint[A, B, C, CB, CC, Alg, V] =
    new AlgebaricAndConstraint


  /**
   * Constraint: attaches a custom description to the given constraint. Useful when a constraint alias need a more
   * accurate description.
   *
   * @tparam B the wrapped constraint's dummy
   * @tparam V the description to attach. Must be litteral.
   * @note This constraint is runtime only.
   */
  trait DescribedAs[B, V]

  class DescribedAsConstraint[A, B, C <: Constraint[A, B], V <: String](using constraint: C) extends Constraint.RuntimeOnly[A, DescribedAs[B, V]] {

    override inline def assert(value: A): Boolean = constraint.assert(value)

    override inline def getMessage(value: A): String = constValue[V]
  }

  inline given[A, B, C <: Constraint[A, B], V <: String](using C): DescribedAsConstraint[A, B, C, V] = new DescribedAsConstraint


  /**
   * Constraint: makes the wrapped constraint runtime-only.
   * @tparam B the wrapped constraint's dummy
   */
  trait RuntimeOnly[B]

  class RuntimeOnlyConstraint[A, B, C <: Constraint[A, B]](using constraint: C) extends Constraint.RuntimeOnly[A, RuntimeOnly[B]] {

    override inline def assert(value: A): Boolean = constraint.assert(value)

    override inline def getMessage(value: A): String = constraint.getMessage(value)
  }

  inline given[A, B, C <: Constraint[A, B]](using C): RuntimeOnlyConstraint[A, B, C] = new RuntimeOnlyConstraint

  /**
   * Represent a constraint with a placehold value as input (instead of the Constrained value). Used for chained algebra.
   * @tparam B the wrapped constraint
   * @tparam Alg the algebra type of this placehold
   * @tparam V the value to pass as input
   */
  trait Placehold[B, Alg, V] extends AlgebraPart[Alg, V]

  class PlaceholdConstraint[A, B, C <: Constraint[A, B], Alg, V <: A](using constraint: C) extends Constraint[A, Placehold[B, Alg, V]] {

    override inline def assert(value: A): Boolean = constraint.assert(constValue[V])

    override inline def getMessage(value: A): String = constraint.getMessage(constValue[V])
  }

  inline given[A, B, C <: Constraint[A, B], Alg, V <: A](using C): PlaceholdConstraint[A, B, C, Alg, V] = new PlaceholdConstraint
}