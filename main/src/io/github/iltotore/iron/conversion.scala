package io.github.iltotore.iron

import io.github.iltotore.iron.constraint.collection.ForAll

import scala.language.implicitConversions
import scala.util.boundary
import scala.util.boundary.break

/**
 * Implicitly refine at compile-time the given value.
 *
 * @param value the value to refine.
 * @param constraint the implementation of `C` to check.
 * @tparam A the refined type.
 * @tparam C the constraint applied to the type.
 * @return the given value typed as [[IronType]]
 *
 * @note This method ensures that the value satisfies the constraint. If it doesn't or isn't evaluable at compile-time, the compilation is aborted.
 */
implicit inline def autoRefine[A, C](inline value: A)(using inline constraint: Constraint[A, C]): A :| C =
  macros.assertCondition(value, constraint.test(value), constraint.message)
  IronType(value)

/**
 * Implicitly cast a constrained value to another if verified.
 *
 * @param value the refined to value to cast.
 * @param `C1 ==> C2` the evidence that the original constraint `C1` implies `C2`.
 * @tparam A the refined type.
 * @tparam C1 the original constraint.
 * @tparam C2 the target constraint.
 * @return the given value constrained by `C2`.
 */
implicit inline def autoCastIron[A, C1, C2](inline value: A :| C1)(using C1 ==> C2): A :| C2 = value.asInstanceOf

/**
 * Implicitly cast an iterable of elements constrained by `C1` into an iterable constrained by `ForAll[C2]` if `C1` implies `C2`.
 * @param iterable the iterable to factorize.
 * @param `C1 ==> C2` the evidence that `C1` implies `C2`.
 * @tparam A the refined type.
 * @tparam I the iterable type.
 * @tparam C1 the original constraint.
 * @tparam C2 the target constraint.
 * @return the given value as instance of `I[A] :| ForAll[C2]`.
 * @see [[autoDistribute]]
 */
implicit inline def autoFactorize[A, I[_] <: Iterable[?], C1, C2](inline iterable: I[A :| C1])(using C1 ==> C2): I[A] :| ForAll[C2] =
  iterable.asInstanceOf

/**
 * Implicitly cast an iterable constrained by `ForAll[C1]` into an iterable of elements constrained by `C2` if `C1` implies `C2`.
 * @param iterable the iterable to factorize.
 * @param `C1 ==> C2` the evidence that `C1` implies `C2`.
 * @tparam A the refined type.
 * @tparam I the iterable type.
 * @tparam C1 the original constraint.
 * @tparam C2 the target constraint.
 * @return the given value as instance of `I[A :| C2]`.
 * @see [[autoFactorize]]
 */
implicit inline def autoDistribute[A, I[_] <: Iterable[?], C1, C2](inline iterable: I[A] :| ForAll[C1])(using C1 ==> C2): I[A :| C2] =
  iterable.asInstanceOf

extension [A, C1](value: A :| C1)

  /**
   * Refine the given value again, assuming the constraint holds.
   *
   * @return a constrained value, without performing constraint checks.
   * @see [[assume]], [[assumeAllFurther]].
   */
  inline def assumeFurther[C2]: A :| (C1 & C2) = (value: A).assume[C1 & C2]

  /**
   * Refine the given value again at runtime.
   *
   * @param constraint the new constraint to test.
   * @return this value refined with `C1 & C2`.
   * @throws an [[IllegalArgumentException]] if the constraint is not satisfied.
   * @see [[refineUnsafe]].
   */
  @deprecated("Use refineFurtherUnsafe instead. refineFurther will be removed in 3.0")
  inline def refineFurther[C2](using inline constraint: Constraint[A, C2]): A :| (C1 & C2) =
    refineFurtherUnsafe[C2]

  /**
   * Refine the given value again at runtime.
   *
   * @param constraint the new constraint to test.
   * @return this value refined with `C1 & C2`.
   * @throws an [[IllegalArgumentException]] if the constraint is not satisfied.
   * @see [[refineUnsafe]].
   */
  inline def refineFurtherUnsafe[C2](using inline constraint: Constraint[A, C2]): A :| (C1 & C2) =
    (value: A).refineUnsafe[C2].assumeFurther[C1]

  /**
   * Refine the given value again at runtime, resulting in an [[Either]].
   *
   * @param constraint the new constraint to test.
   * @return a [[Right]] containing this value refined with `C1 & C2` or a [[Left]] containing the constraint message.
   * @see [[refineEither]].
   */
  inline def refineFurtherEither[C2](using inline constraint: Constraint[A, C2]): Either[String, A :| (C1 & C2)] =
    (value: A).refineEither[C2].map(_.assumeFurther[C1])

  /**
   * Refine the given value again at runtime, resulting in an [[Option]].
   *
   * @param constraint the new constraint to test.
   * @return a [[Option]] containing this value refined with `C1 & C2` or [[None]].
   * @see [[refineOption]].
   */
  inline def refineFurtherOption[C2](using inline constraint: Constraint[A, C2]): Option[A :| (C1 & C2)] =
    (value: A).refineOption[C2].map(_.assumeFurther[C1])

extension [F[_], A, C1](wrapper: F[A :| C1])

  /**
   * Refine the given value(s) again, assuming the constraint holds.
   *
   * @return the constrained values, without performing constraint checks.
   * @see [[assume]], [[assumeFurther]].
   */
  inline def assumeAllFurther[C2]: F[A :| (C1 & C2)] = wrapper.asInstanceOf[F[A :| (C1 & C2)]]

  /**
   * Refine the given value(s) again at runtime.
   *
   * @param constraint the new constraint to test.
   * @return the given values refined with `C1 & C2`.
   * @throws IllegalArgumentException if the constraint is not satisfied.
   * @see [[refineUnsafe]], [[refineFurtherUnsafe]].
   */
  inline def refineAllFurtherUnsafe[C2](using mapLogic: MapLogic[F], inline constraint: Constraint[A, C2]): F[A :| (C1 & C2)] =
    mapLogic.map(wrapper, _.refineFurtherUnsafe[C2])

  /**
   * Refine the given value(s) again at runtime, resulting in an [[Either]].
   *
   * @param constraint the new constraint to test.
   * @return a [[Right]] containing the given values refined with `C1 & C2` or a [[Left]] containing the constraint message.
   * @see [[refineEither]], [[refineAllFurtherEither]].
   */
  inline def refineAllFurtherEither[C2](using mapLogic: MapLogic[F], inline constraint: Constraint[A, C2]): Either[String, F[A :| (C1 & C2)]] =
    boundary:
      Right(mapLogic.map(
        wrapper,
        _.refineFurtherEither[C2] match
          case Right(value) => value
          case Left(error)  => break(Left(error))
      ))

  /**
   * Refine the given value(s) again at runtime, resulting in an [[Option]].
   *
   * @param constraint the new constraint to test.
   * @return a [[Option]] containing the given values refined with `C1 & C2` or [[None]].
   * @see [[refineOption]], [[refineFurtherOption]].
   */
  inline def refineAllFurtherOption[C2](using mapLogic: MapLogic[F], inline constraint: Constraint[A, C2]): Option[F[A :| (C1 & C2)]] =
    boundary:
      Some(mapLogic.map(
        wrapper,
        _.refineFurtherOption[C2] match
          case Some(value) => value
          case None        => break(None)
      ))

extension [A, C1, C2](value: A :| C1 :| C2)
  /**
   * Transform `A :| C1 :| C2` to `A :| (C1 & C2)`
   */
  inline def compose: A :| (C1 & C2) = (value: A).assume[C1 & C2]

extension [A, C1, C2](value: A :| (C1 & C2))
  /**
   * Transform `A :| (C1 & C2)` to `A :| C1 :| C2`
   */
  inline def decompose: A :| C1 :| C2 = (value: A).assume[C1].assume[C2]
