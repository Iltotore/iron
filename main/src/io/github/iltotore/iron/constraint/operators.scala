package io.github.iltotore.iron.constraint

import io.github.iltotore.iron.{==>, Constraint, Implication}

import scala.compiletime.ops.boolean
import scala.compiletime.summonInline

/**
 * Constraint operators (e.g [[operators.Not]]...).
 */
object operators:

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
