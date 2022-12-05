package io.github.iltotore.iron.constraint

import io.github.iltotore.iron.{==>, Constraint, Implication}
import io.github.iltotore.iron.compileTime.stringValue

import scala.compiletime.{constValue, summonInline}
import scala.compiletime.ops.any.ToString
import scala.compiletime.ops.boolean

/**
 * Constraints working for any type (e.g [[any.StrictEqual]]).
 */
object any:

  /**
   * A constraint decorator with a custom description.
   *
   * @tparam C the decorated constraint.
   * @tparam V the description to attach.
   * @example {{{
   * //Literal
   * type PosInt = Greater[0] DescribedAs "Should be positive"
   *
   * //Using type-level String concatenation (example taken from `numeric`)
   * import io.github.iltotore.iron.ops.*
   *
   * type GreaterEqual[V] = (Greater[V] || StrictEqual[V]) DescribedAs ("Should be greater than or equal to " + V)
   * }}}
   */
  final class DescribedAs[C, V <: String]

  /**
   * Tests strict equality with the given value.
   *
   * @tparam V the value the input must be equal to.
   */
  final class StrictEqual[V]

  object DescribedAs:
    class DescribedAsConstraint[A, C, Impl <: Constraint[A, C], V <: String](using Impl) extends Constraint[A, DescribedAs[C, V]]:

      override inline def test(value: A): Boolean = summonInline[Impl].test(value)

      override inline def message: String = constValue[V]

    inline given [A, C, Impl <: Constraint[A, C], V <: String](using inline constraint: Impl): DescribedAsConstraint[A, C, Impl, V] =
      new DescribedAsConstraint

    /**
     * A described constraint C1 implies C1.
     */
    given [C1, C2, V <: String](using C1 ==> C2): ((C1 DescribedAs V) ==> C2) = Implication()

    /**
     * A constraint C1 implies its "described" form.
     */
    given [C1, C2, V <: String](using C1 ==> C2): (C1 ==> (C2 DescribedAs V)) = Implication()

  object StrictEqual:
    inline given [A, V <: A]: Constraint[A, StrictEqual[V]] with

      override inline def test(value: A): Boolean = value == constValue[V]

      override inline def message: String = "Should strictly equal to " + constValue[ToString[V]]
