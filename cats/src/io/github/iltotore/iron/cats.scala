package io.github.iltotore.iron

import io.github.iltotore.iron.constraint.numeric.{Greater, Less}

import _root_.cats.{Eq, Order, Show}
import _root_.cats.data.{EitherNec, EitherNel, NonEmptyChain, NonEmptyList, Validated, ValidatedNec, ValidatedNel}
import _root_.cats.kernel.{Hash, LowerBounded, PartialOrder, UpperBounded}
import _root_.cats.syntax.either.*
import Validated.{Valid, Invalid}

object cats extends IronCatsInstances:

  /**
   * Utility methods for the Cats library.
   */
  extension [A](value: A)

    /**
     * Refine the given value at runtime, resulting in an [[EitherNec]].
     *
     * @param constraint the constraint to test with the value to refine.
     * @return a [[Right]] containing this value as [[IronType]] or a [[Left]] containing the constraint message.
     * @see [[refineEither]], [[refineNel]].
     */
    inline def refineNec[C](using inline constraint: Constraint[A, C]): EitherNec[String, A :| C] =
      value.refineEither[C].toEitherNec

    /**
     * Refine the given value at runtime, resulting in an [[EitherNel]].
     *
     * @param constraint the constraint to test with the value to refine.
     * @return a [[Right]] containing this value as [[IronType]] or a [[Left]] containing the constraint message.
     * @see [[refineEither]], [[refineNec]].
     */
    inline def refineNel[C](using inline constraint: Constraint[A, C]): EitherNel[String, A :| C] =
      value.refineEither[C].toEitherNel

    /**
     * Refine the given value at runtime, resulting in a [[Validated]].
     *
     * @param constraint the constraint to test with the value to refine.
     * @return a [[Valid]] containing this value as [[IronType]] or an [[Invalid]] containing the constraint message.
     * @see [[refineValidatedNec]], [[refineValidatedNel]].
     */
    inline def refineValidated[C](using inline constraint: Constraint[A, C]): Validated[String, A :| C] =
      Validated.cond(constraint.test(value), value.asInstanceOf[A :| C], constraint.message)

    /**
     * Refine the given value applicatively at runtime, resulting in a [[ValidatedNec]].
     *
     * @param constraint the constraint to test with the value to refine.
     * @return a [[Valid]] containing this value as [[IronType]] or an [[Invalid]] containing a [[NonEmptyChain]] of error messages.
     * @see [[refineValidated]], [[refineValidatedNel]].
     */
    inline def refineValidatedNec[C](using inline constraint: Constraint[A, C]): ValidatedNec[String, A :| C] =
      Validated.condNec(constraint.test(value), value.asInstanceOf[A :| C], constraint.message)

    /**
     * Refine the given value applicatively at runtime, resulting in a [[ValidatedNel]].
     *
     * @param constraint the constraint to test with the value to refine.
     * @return a [[Valid]] containing this value as [[IronType]] or an [[Invalid]] containing a [[NonEmptyList]] of error messages.
     * @see [[refineValidated]], [[refineValidatedNec]].
     */
    inline def refineValidatedNel[C](using inline constraint: Constraint[A, C]): ValidatedNel[String, A :| C] =
      Validated.condNel(constraint.test(value), value.asInstanceOf[A :| C], constraint.message)

private trait IronCatsInstances extends IronCatsLowPriority:
  inline given [A, C](using inline ev: Eq[A]): Eq[A :| C] = ev.asInstanceOf[Eq[A :| C]]
  inline given [A, C](using inline ev: Order[A]): Order[A :| C] = ev.asInstanceOf[Order[A :| C]]
  inline given [A, C](using inline ev: PartialOrder[A]): PartialOrder[A :| C] = ev.asInstanceOf[PartialOrder[A :| C]]
  inline given [A, C](using inline ev: Show[A]): Show[A :| C] = ev.asInstanceOf[Show[A :| C]]
  inline given [A, C, V](using inline ev: LowerBounded[A], implication: C ==> Greater[V]): LowerBounded[A :| C] = ev.asInstanceOf[LowerBounded[A :| C]]
  inline given [A, C, V](using inline ev: UpperBounded[A], implication: C ==> Greater[V]): UpperBounded[A :| C] = ev.asInstanceOf[UpperBounded[A :| C]]

private trait IronCatsLowPriority:
  inline given [A, C](using inline ev: Hash[A]): Hash[A :| C] = ev.asInstanceOf[Hash[A :| C]]
