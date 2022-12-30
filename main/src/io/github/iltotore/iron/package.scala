package io.github.iltotore.iron

import io.github.iltotore.iron.macros

import scala.Console.{CYAN, RESET}
import scala.compiletime.{codeOf, error}
import scala.util.NotGiven

/**
 * The main package of Iron. Contains:
 * - IronType
 * - Refining methods (both compile-time and runtime)
 * - Export of [[constraint.any]].*
 */

export io.github.iltotore.iron.constraint.any.*

/**
 * An Iron type (refined).
 *
 * @tparam A the underlying type.
 * @tparam C the predicate/constraint guarding this type.
 */
opaque type IronType[A, C] <: A = A

/**
 * Alias for [[IronType]]. Similar to the mathematical symbol `|` in e.g `{x in R | x > 0}`.
 */
type :|[A, C] = IronType[A, C]

object IronType:

  /**
   * Create an IronType.
   *
   * @param value the value to be constrained.
   * @tparam A the refined type.
   * @tparam C the constraint applied to the type.
   * @return the given value typed as [[IronType]].
   * @note this does not check if the constraint is satisfied. Use [[package.refine]] to refine a value at runtime.
   * @see [[package.autoRefine]], [[package.refine]], [[package.refineEither]], [[package.refineOption]]
   */
  inline def apply[A, C](value: A): IronType[A, C] = value

end IronType

extension [A](value: A)

  /**
   * Refine the given value at runtime.
   *
   * @param constraint the constraint to test with the value to refine.
   * @return this value as [[IronType]].
   * @throws an [[IllegalArgumentException]] if the constraint is not satisfied.
   * @see [[autoRefine]], [[refineEither]], [[refineOption]].
   */
  inline def refine[B](using inline constraint: Constraint[A, B]): A :| B =
    if constraint.test(value) then value
    else throw IllegalArgumentException(constraint.message)

  /**
   * Refine the given value at runtime, resulting in an [[Either]].
   *
   * @param constraint the constraint to test with the value to refine.
   * @return a [[Right]] containing this value as [[IronType]] or a [[Left]] containing the constraint message.
   * @see [[autoRefine]], [[refine]], [[refineOption]].
   */
  inline def refineEither[B](using inline constraint: Constraint[A, B]): Either[String, A :| B] =
    Either.cond(constraint.test(value), value, constraint.message)

  /**
   * Refine the given value at runtime, resulting in an [[Option]].
   *
   * @param constraint the constraint to test with the value to refine.
   * @return an Option containing this value as [[IronType]] or [[None]].
   * @see [[autoRefine]], [[refine]], [[refineEither]].
   */
  inline def refineOption[B](using inline constraint: Constraint[A, B]): Option[A :| B] =
    Option.when(constraint.test(value))(value)
