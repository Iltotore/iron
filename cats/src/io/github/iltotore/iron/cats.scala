package io.github.iltotore.iron

import _root_.cats.{Eq, Monoid, Order, Semigroup, Show}
import _root_.cats.data.{EitherNec, EitherNel, NonEmptyChain, NonEmptyList, Validated, ValidatedNec, ValidatedNel}
import _root_.cats.kernel.{
  Band,
  BoundedSemilattice,
  CommutativeGroup,
  CommutativeMonoid,
  CommutativeSemigroup,
  Group,
  Hash,
  LowerBounded,
  PartialOrder,
  Semilattice,
  UpperBounded
}
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
    inline def refineNec[B](using inline constraint: Constraint[A, B]): EitherNec[String, A :| B] =
      value.refineEither[B].toEitherNec

    /**
     * Refine the given value at runtime, resulting in an [[EitherNel]].
     *
     * @param constraint the constraint to test with the value to refine.
     * @return a [[Right]] containing this value as [[IronType]] or a [[Left]] containing the constraint message.
     * @see [[refineEither]], [[refineNec]].
     */
    inline def refineNel[B](using inline constraint: Constraint[A, B]): EitherNel[String, A :| B] =
      value.refineEither[B].toEitherNel

    /**
     * Refine the given value at runtime, resulting in a [[Validated]].
     *
     * @param constraint the constraint to test with the value to refine.
     * @return a [[Valid]] containing this value as [[IronType]] or an [[Invalid]] containing the constraint message.
     * @see [[refineValidatedNec]], [[refineValidatedNel]].
     */
    inline def refineValidated[B](using inline constraint: Constraint[A, B]): Validated[String, A :| B] =
      Validated.cond(constraint.test(value), value.asInstanceOf[A :| B], constraint.message)

    /**
     * Refine the given value applicatively at runtime, resulting in a [[ValidatedNec]].
     *
     * @param constraint the constraint to test with the value to refine.
     * @return a [[Valid]] containing this value as [[IronType]] or an [[Invalid]] containing a [[NonEmptyChain]] of error messages.
     * @see [[refineValidated]], [[refineValidatedNel]].
     */
    inline def refineValidatedNec[B](using inline constraint: Constraint[A, B]): ValidatedNec[String, A :| B] =
      Validated.condNec(constraint.test(value), value.asInstanceOf[A :| B], constraint.message)

    /**
     * Refine the given value applicatively at runtime, resulting in a [[ValidatedNel]].
     *
     * @param constraint the constraint to test with the value to refine.
     * @return a [[Valid]] containing this value as [[IronType]] or an [[Invalid]] containing a [[NonEmptyList]] of error messages.
     * @see [[refineValidated]], [[refineValidatedNec]].
     */
    inline def refineValidatedNel[B](using inline constraint: Constraint[A, B]): ValidatedNel[String, A :| B] =
      Validated.condNel(constraint.test(value), value.asInstanceOf[A :| B], constraint.message)

private trait IronCatsInstances extends IronCatsLowPriority0:
  inline given [A, B](using inline ev: Band[A]): Band[A :| B] = ev.asInstanceOf[Band[A :| B]]
  inline given [A, B](using inline ev: BoundedSemilattice[A]): BoundedSemilattice[A :| B] = ev.asInstanceOf[BoundedSemilattice[A :| B]]
  inline given [A, B](using inline ev: CommutativeGroup[A]): CommutativeGroup[A :| B] = ev.asInstanceOf[CommutativeGroup[A :| B]]
  inline given [A, B](using inline ev: CommutativeMonoid[A]): CommutativeMonoid[A :| B] = ev.asInstanceOf[CommutativeMonoid[A :| B]]
  inline given [A, B](using inline ev: CommutativeSemigroup[A]): CommutativeSemigroup[A :| B] = ev.asInstanceOf[CommutativeSemigroup[A :| B]]
  inline given [A, B](using inline ev: Eq[A]): Eq[A :| B] = ev.asInstanceOf[Eq[A :| B]]
  inline given [A, B](using inline ev: Group[A]): Group[A :| B] = ev.asInstanceOf[Group[A :| B]]
  inline given [A, B](using inline ev: LowerBounded[A]): LowerBounded[A :| B] = ev.asInstanceOf[LowerBounded[A :| B]]
  inline given [A, B](using inline ev: Monoid[A]): Monoid[A :| B] = ev.asInstanceOf[Monoid[A :| B]]
  inline given [A, B](using inline ev: Order[A]): Order[A :| B] = ev.asInstanceOf[Order[A :| B]]
  inline given [A, B](using inline ev: PartialOrder[A]): PartialOrder[A :| B] = ev.asInstanceOf[PartialOrder[A :| B]]
  inline given [A, B](using inline ev: Semigroup[A]): Semigroup[A :| B] = ev.asInstanceOf[Semigroup[A :| B]]
  inline given [A, B](using inline ev: Semilattice[A]): Semilattice[A :| B] = ev.asInstanceOf[Semilattice[A :| B]]
  inline given [A, B](using inline ev: Show[A]): Show[A :| B] = ev.asInstanceOf[Show[A :| B]]
  inline given [A, B](using inline ev: UpperBounded[A]): UpperBounded[A :| B] = ev.asInstanceOf[UpperBounded[A :| B]]

private trait IronCatsLowPriority0:
  inline given [A, B](using inline ev: Hash[A]): Hash[A :| B] = ev.asInstanceOf[Hash[A :| B]]
