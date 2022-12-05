package io.github.iltotore.iron

import io.github.iltotore.iron.macros.union.*

import scala.annotation.implicitNotFound

/**
 * An algebraic implication between two constraints (e.g transitivity for [[package.constraint.numeric.Greater]]).
 *
 * @tparam C1 the assumed constraint.
 * @tparam C2 the constraint implied by `C1`.
 */
@implicitNotFound("Could not prove that ${C1} implies ${C2}")
final class Implication[C1, C2]

/**
 * Alias for [[Implication]]. Similar to the mathematical implication symbol, often represented by an arrow.
 */
type ==>[C1, C2] = Implication[C1, C2]

object Implication:
  /**
   * The "self" implication "C ==> C".
   *
   * @tparam C any constraint.
   */
  given [C]: (C ==> C) = Implication()

  /**
   * If [[C1]] is a subtype of [[C2]] then [[C1]] implies [[C2]].
   * Used for union constraint `C1 ==> C1 | C2`
   *
   * @tparam C1 any constraint
   * @tparam C2 any constraint parent of [[C1]]
   */
  given [C1, C2](using C1 <:< C2): (C1 ==> C2) = Implication()

  transparent inline given [C1, C2](using IsUnion[C1]): (C1 ==> C2) = unionImplication[C1, C2]
