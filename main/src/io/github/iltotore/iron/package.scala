package io.github.iltotore.iron

import io.github.iltotore.iron.macros

import scala.Console.{CYAN, RESET}
import scala.compiletime.{codeOf, error, summonInline}
import scala.reflect.TypeTest
import scala.util.{boundary, NotGiven}
import scala.util.boundary.break

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
   * @note this does not check if the constraint is satisfied. Use [[package.refineUnsafe]] to refine a value at runtime.
   * @see [[package.autoRefine]], [[package.refineUnsafe]], [[package.refineEither]], [[package.refineOption]]
   */
  inline def apply[A, C](value: A): IronType[A, C] = value

end IronType

extension [A](value: A)

  /**
   * Refine the given value, assuming the constraint holds.
   *
   * @return a constrained value, without performing constraint checks.
   * @see [[assumeAll]], [[autoRefine]], [[refineUnsafe]].
   */
  inline def assume[B]: A :| B = value

  /**
   * Refine the given value at runtime.
   *
   * @param constraint the constraint to test with the value to refine.
   * @return this value as [[IronType]].
   * @throws an [[IllegalArgumentException]] if the constraint is not satisfied.
   * @see [[autoRefine]], [[refineEither]], [[refineOption]].
   */
  @deprecated("Use refineUnsafe instead. refine will be removed in 3.0.0")
  inline def refine[B](using inline constraint: Constraint[A, B]): A :| B =
    refineUnsafe[B]

  /**
   * Refine the given value at runtime.
   *
   * @param constraint the constraint to test with the value to refine.
   * @return this value as [[IronType]].
   * @throws an [[IllegalArgumentException]] if the constraint is not satisfied.
   * @see [[autoRefine]], [[refineEither]], [[refineOption]].
   */
  inline def refineUnsafe[B](using inline constraint: Constraint[A, B]): A :| B =
    if constraint.test(value) then value
    else throw IllegalArgumentException(constraint.message)

  /**
   * Refine the given value at runtime, resulting in an [[Either]].
   *
   * @param constraint the constraint to test with the value to refine.
   * @return a [[Right]] containing this value as [[IronType]] or a [[Left]] containing the constraint message.
   * @see [[autoRefine]], [[refineUnsafe]], [[refineOption]].
   */
  inline def refineEither[B](using inline constraint: Constraint[A, B]): Either[String, A :| B] =
    Either.cond(constraint.test(value), value, constraint.message)

  /**
   * Refine the given value at runtime, resulting in an [[Option]].
   *
   * @param constraint the constraint to test with the value to refine.
   * @return an Option containing this value as [[IronType]] or [[None]].
   * @see [[autoRefine]], [[refineUnsafe]], [[refineEither]].
   */
  inline def refineOption[B](using inline constraint: Constraint[A, B]): Option[A :| B] =
    Option.when(constraint.test(value))(value)

extension [F[_], A](wrapper: F[A])

  /**
   * Refine the contained value(s), assuming the constraint holds.
   *
   * @return constrained values, without performing constraint checks.
   * @see [[assume]], [[autoRefine]], [[refineUnsafe]].
   */
  inline def assumeAll[B]: F[A :| B] = wrapper

  /**
   * Refine the given value(s) at runtime.
   *
   * @param constraint the constraint to test with the value to refine.
   * @return the given values as [[IronType]].
   * @throws an [[IllegalArgumentException]] if the constraint is not satisfied.
   * @see [[refineUnsafe]].
   */
  inline def refineAllUnsafe[B](using mapLogic: MapLogic[F], inline constraint: Constraint[A, B]): F[A :| B] =
    mapLogic.map(wrapper, _.refineUnsafe[B])

  /**
   * Refine the given value(s) at runtime, resulting in an [[Either]].
   *
   * @param constraint the constraint to test with the value to refine.
   * @return a [[Right]] containing the given values as [[IronType]] or a [[Left]] containing the constraint message.
   * @see [[refineEither]].
   */
  inline def refineAllEither[B](using mapLogic: MapLogic[F], inline constraint: Constraint[A, B]): Either[String, F[A :| B]] =
    boundary:
      Right(mapLogic.map(
        wrapper,
        _.refineEither[B] match
          case Right(value) => value
          case Left(error)  => break(Left(error))
      ))

  /**
   * Refine the given value(s) at runtime, resulting in an [[Option]].
   *
   * @param constraint the constraint to test with the value to refine.
   * @return a [[Some]] containing the given values as [[IronType]] or [[None]].
   * @see [[refineOption]].
   */
  inline def refineAllOption[B](using mapLogic: MapLogic[F], inline constraint: Constraint[A, B]): Option[F[A :| B]] =
    boundary:
      Some(mapLogic.map(
        wrapper,
        _.refineOption[B] match
          case Some(value) => value
          case None        => break(None)
      ))
