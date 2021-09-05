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

  extension [A](a: A) {

    /**
     * Ensure that `a` passes B's constraint
     *
     * @tparam B the constraint's dummy
     * @return the value as Constrained
     * @see [[refineValue]]
     */
    def refined[B](using Constraint[A, B]): Constrained[A, B] = refineValue[A, B, Constraint[A, B]](a)
  }

  /**
   * Constraint: checks if the input value strictly equals to V.
   *
   * @tparam V
   */
  trait StrictEqual[V]

  type ==[A, V] = A ==> StrictEqual[V]

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

  type \[A, V] = A ==> Not[StrictEqual[V]]

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
}