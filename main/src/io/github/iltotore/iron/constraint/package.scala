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
  implicit inline def valueToConstrained[A, B](value: A)(using inline constraint: Constraint[A, B]): Constrained[A, B] = {
    Constrained(compileTime.preAssert(value, constraint))
  }

  /**
   * Constraint: checks if the input value strictly equals to V.
   * @tparam V
   */
  trait StrictEqual[V]
  type ==[A, V] = A ==> StrictEqual[V]

  class StrictEqualConstraint[A, V <: A] extends Constraint[A, StrictEqual[V]] {

    override inline def assert(value: A): Boolean = value == constValue[V]
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
  }

  inline given[A, V <: A]: EqualConstraint[A, V] = new EqualConstraint


  /**
   * Constraint: checks if the input value doesn't pass B's constraint.
   *
   * @tparam B the "inverted" constraint's dummy.
   * @note use the [[Not]] alias instead of [[NotDummy]] directly: {{{
   *          //Bad
   *          def inverse(x: Double ==> NotDummy[StrictEqual[0]]): Double = 1/x
   *
   *          //Good
   *          def inverse(x: Double ==> Not[StrictEqual[0]]): Double = 1/x
   *
   *          //Even better
   *          def inverse(x: Double ==> Not[0]): Double = 1/x
   * }}}
   */
  trait NotDummy[B]

  /**
   * Proxy for NotDummy[B] if B has a constraint. Not[StrictEqual[B]] otherwise.
   *
   * @tparam B the constraint's dummy or the value used for StrictEqual[B]
   */
  type Not[B] = B match {

    case Constraint[_, _] => NotDummy[B]

    case _ => NotDummy[StrictEqual[B]]
  }

  class NotConstraint[A, B, C <: Constraint[A, B]](using constraint: C) extends Constraint[A, NotDummy[B]] {

    override inline def assert(value: A): Boolean = !constraint.assert(value)
  }

  inline given[A, B, C <: Constraint[A, B]](using C): NotConstraint[A, B, C] = new NotConstraint
}