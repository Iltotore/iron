package io.github.iltotore.iron.constraint

import io.github.iltotore.iron.{==>, Constraint, Implication}
import io.github.iltotore.iron.compileTime.stringValue

import scala.compiletime.{constValue, summonInline}
import scala.compiletime.ops.any.ToString
import scala.compiletime.ops.boolean

/**
 * Constraints working for any type (e.g [[any.StrictEqual]]) and constraint operators (e.g [[any.Not]]...).
 */
object any:

  /**
   * An always-valid constraint.
   */
  final class True

  /**
   * An always-invalid constraint.
   */
  final class False

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
   * A constraint decorator acting like a boolean "not".
   * @tparam C the decorated constraint.
   */
  final class Not[C]

  /**
   * Alias for [[Not]].
   */
  type ![C] = C match
    case Boolean => boolean.![C]
    case _       => Not[C]

  /**
   * Tests strict equality with the given value.
   *
   * @tparam V the value the input must be equal to.
   */
  final class StrictEqual[V]

  object True:

    inline given [A]: Constraint[A, True] with

      override inline def test(value: A): Boolean = true

      override inline def message: String = "Always valid"

    /**
     * True implies Not[False]
     */
    given (True ==> Not[False]) = Implication()

  object False:

    inline given [A]: Constraint[A, False] with

      override inline def test(value: A): Boolean = false

      override inline def message: String = "Always invalid"

    /**
     * False implies Not[True]
     */
    given (False ==> Not[True]) = Implication()

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

  object Not:
    class NotConstraint[A, C, Impl <: Constraint[A, C]](using Impl) extends Constraint[A, Not[C]]:

      override inline def test(value: A): Boolean =
        !summonInline[Impl].test(value)

      override inline def message: String =
        "!(" + summonInline[Impl].message + ")"

    inline given [A, C, Impl <: Constraint[A, C]](using inline constraint: Impl): NotConstraint[A, C, Impl] = new NotConstraint

    /**
     * Doubly inverted C implies C.
     */
    given [C1, C2](using C1 ==> C2): (Not[Not[C1]] ==> C2) = Implication()

    /**
     * C implies doubly inverted C.
     */
    given [C1, C2](using C1 ==> C2): (C1 ==> Not[Not[C2]]) = Implication()

  object StrictEqual:
    inline given [A, V <: A]: Constraint[A, StrictEqual[V]] with

      override inline def test(value: A): Boolean = value == constValue[V]

      override inline def message: String = "Should strictly equal to " + constValue[ToString[V]]
