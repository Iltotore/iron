package io.github.iltotore.iron

import _root_.cats.data.{EitherNec, EitherNel, Validated, ValidatedNec, ValidatedNel}
import _root_.cats.syntax.either.*
import _root_.cats.data.Validated.{Invalid, Valid}
import _root_.cats.Functor
import _root_.cats.implicits.*
import io.github.iltotore.iron.constraint.numeric.{Greater, Less, Negative, Positive}

import scala.util.NotGiven
import scala.util.boundary
import scala.util.boundary.break
import _root_.cats.Traverse

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

  extension [F[_], A](wrapper: F[A])

    /**
     * Refine the wrapped value(s) at runtime, accumulating errors.
     *
     * @param constraint the constraint to test with the value to refine.
     * @return a [[Right]] containing this value as [[IronType]] or an [[Left]] containing a [[NonEmptyChain]] of errors.
     * @see [[refineNec]].
     */
    inline def refineAllNec[C](using traverse: Traverse[F], inline constraint: Constraint[A, C]): EitherNec[InvalidValue[A], F[A :| C]] =
      wrapper.refineAllValidatedNec[C].toEither

    /**
     * Refine the wrapped value(s) at runtime, accumulating errors.
     *
     * @param constraint the constraint to test with the value to refine.
     * @return a [[Right]] containing this value as [[IronType]] or an [[Left]] containing a [[NonEmptyList]] of errors.
     * @see [[refineNec]].
     */
    inline def refineAllNel[C](using traverse: Traverse[F], inline constraint: Constraint[A, C]): EitherNel[InvalidValue[A], F[A :| C]] =
      wrapper.refineAllValidatedNel[C].toEither

    /**
     * Refine the wrapped value(s) at runtime, accumulating errors.
     *
     * @param constraint the constraint to test with the value to refine.
     * @return a [[Valid]] containing this value as [[IronType]] or an [[Invalid]] containing a [[NonEmptyChain]] of errors.
     * @see [[refineValidatedNec]].
     */
    inline def refineAllValidatedNec[C](using traverse: Traverse[F], inline constraint: Constraint[A, C]): ValidatedNec[InvalidValue[A], F[A :| C]] =
      traverse.traverse(wrapper): value =>
        Validated.condNec[InvalidValue[A], A :| C](constraint.test(value), value.assume[C], InvalidValue(value, constraint.message))

    /**
     * Refine the wrapped value(s) at runtime, accumulating errors.
     *
     * @param constraint the constraint to test with the value to refine.
     * @return a [[Valid]] containing this value as [[IronType]] or an [[Invalid]] containing a [[NonEmptyList]] of errors.
     * @see [[refineValidatedNel]].
     */
    inline def refineAllValidatedNel[C](using traverse: Traverse[F], inline constraint: Constraint[A, C]): ValidatedNel[InvalidValue[A], F[A :| C]] =
      traverse.traverse(wrapper): value =>
        Validated.condNel[InvalidValue[A], A :| C](constraint.test(value), value.assume[C], InvalidValue(value, constraint.message))

  extension [A, C1](value: A :| C1)

    /**
     * Refine the given value again at runtime, resulting in an [[EitherNec]].
     *
     * @param constraint the new constraint to test.
     * @return a [[Right]] containing this refined with `C1 & C2` or a [[Left]] containing the constraint message.
     * @see [[refineNec]].
     */
    inline def refineFurtherNec[C2](using inline constraint: Constraint[A, C2]): EitherNec[String, A :| (C1 & C2)] =
      value.refineFurtherEither[C2].toEitherNec

    /**
     * Refine the given value again at runtime, resulting in an [[EitherNel]].
     *
     * @param constraint the new constraint to test.
     * @return a [[Right]] containing this refined with `C1 & C2` or a [[Left]] containing the constraint message.
     * @see [[refineNel]].
     */
    inline def refineFurtherNel[C2](using inline constraint: Constraint[A, C2]): EitherNel[String, A :| (C1 & C2)] =
      value.refineFurtherEither[C2].toEitherNel

    /**
     * Refine the given value again at runtime, resulting in an [[Validated]].
     *
     * @param constraint the new constraint to test.
     * @return a [[Validated.Valid]] containing this refined with `C1 & C2` or a [[Validated.Invalid]] containing the constraint message.
     * @see [[refineValidated]].
     */
    inline def refineFurtherValidated[C2](using inline constraint: Constraint[A, C2]): Validated[String, A :| (C1 & C2)] =
      (value: A).refineValidated[C2].map(_.assumeFurther[C1])

    /**
     * Refine the given value again applicatively at runtime, resulting in a [[ValidatedNec]].
     *
     * @param constraint the new constraint to test.
     * @return a [[Valid]] containing this value as [[IronType]] or an [[Invalid]] containing a [[NonEmptyChain]] of error messages.
     * @see [[refineValidatedNec]].
     */
    inline def refineFurtherValidatedNec[C2](using inline constraint: Constraint[A, C2]): ValidatedNec[String, A :| (C1 & C2)] =
      (value: A).refineValidatedNec[C2].map(_.assumeFurther[C1])

    /**
     * Refine the given value again applicatively at runtime, resulting in a [[ValidatedNel]].
     *
     * @param constraint the new constraint to test.
     * @return a [[Valid]] containing this value as [[IronType]] or an [[Invalid]] containing a [[NonEmptyList]] of error messages.
     * @see [[refineValidatedNec]].
     */
    inline def refineFurtherValidatedNel[C2](using inline constraint: Constraint[A, C2]): ValidatedNel[String, A :| (C1 & C2)] =
      (value: A).refineValidatedNel[C2].map(_.assumeFurther[C1])

  extension [F[_], A, C1](wrapper: F[A :| C1])

    /**
     * Refine further the wrapped value(s) at runtime, accumulating errors.
     *
     * @param constraint the constraint to test with the value to refine.
     * @return a [[Right]] containing this value as [[IronType]] or an [[Left]] containing a [[NonEmptyChain]] of errors.
     * @see [[refineFurtherNec]].
     */
    inline def refineAllFurtherNec[C2](using
        traverse: Traverse[F],
        inline constraint: Constraint[A, C2]
    ): EitherNec[InvalidValue[A], F[A :| (C1 & C2)]] =
      wrapper.refineAllFurtherValidatedNec[C2].toEither

    /**
     * Refine further the wrapped value(s) at runtime, accumulating errors.
     *
     * @param constraint the constraint to test with the value to refine.
     * @return a [[Right]] containing this value as [[IronType]] or an [[Left]] containing a [[NonEmptyList]] of errors.
     * @see [[refineFurtherNel]].
     */
    inline def refineAllFurtherNel[C2](using
        traverse: Traverse[F],
        inline constraint: Constraint[A, C2]
    ): EitherNel[InvalidValue[A], F[A :| (C1 & C2)]] =
      wrapper.refineAllFurtherValidatedNel[C2].toEither

    /**
     * Refine further the wrapped value(s) at runtime, accumulating errors.
     *
     * @param constraint the constraint to test with the value to refine.
     * @return a [[Valid]] containing this value as [[IronType]] or an [[Invalid]] containing a [[NonEmptyChain]] of errors.
     * @see [[refineFurtherValidatedNec]].
     */
    inline def refineAllFurtherValidatedNec[C2](using
        traverse: Traverse[F],
        inline constraint: Constraint[A, C2]
    ): ValidatedNec[InvalidValue[A], F[A :| (C1 & C2)]] =
      traverse.traverse(wrapper): value =>
        Validated.condNec[InvalidValue[A], A :| (C1 & C2)](
          constraint.test(value),
          (value: A).assume[C1 & C2],
          InvalidValue(value, constraint.message)
        )

    /**
     * Refine further the wrapped value(s) at runtime, accumulating errors.
     *
     * @param constraint the constraint to test with the value to refine.
     * @return a [[Valid]] containing this value as [[IronType]] or an [[Invalid]] containing a [[NonEmptyList]] of errors.
     * @see [[refineFurtherValidatedNel]].
     */
    inline def refineAllFurtherValidatedNel[C2](using
        traverse: Traverse[F],
        inline constraint: Constraint[A, C2]
    ): ValidatedNel[InvalidValue[A], F[A :| (C1 & C2)]] =
      traverse.traverse(wrapper): value =>
        Validated.condNel[InvalidValue[A], A :| (C1 & C2)](
          constraint.test(value),
          (value: A).assume[C1 & C2],
          InvalidValue(value, constraint.message)
        )

  extension [A, C, T](ops: RefinedTypeOps[A, C, T])

    /**
     * Refine the given value at runtime, resulting in an [[EitherNec]].
     *
     * @param constraint the constraint to test with the value to refine.
     * @return a [[Right]] containing this value as [[IronType]] or a [[Left]] containing the constraint message.
     * @see [[either]], [[eitherNel]].
     */
    def eitherNec(value: A): EitherNec[String, T] = ops.either(value).toEitherNec

    /**
     * Refine the given value at runtime, resulting in an [[EitherNel]].
     *
     * @param constraint the constraint to test with the value to refine.
     * @return a [[Right]] containing this value as [[IronType]] or a [[Left]] containing the constraint message.
     * @see [[either]], [[eitherNec]].
     */
    def eitherNel(value: A): EitherNel[String, T] = ops.either(value).toEitherNel

    /**
     * Refine the given value at runtime, resulting in a [[Validated]].
     *
     * @param constraint the constraint to test with the value to refine.
     * @return a [[Valid]] containing this value as [[IronType]] or an [[Invalid]] containing the constraint message.
     * @see [[validatedNec]], [[validatedNel]].
     */
    def validated(value: A): Validated[String, T] =
      if ops.rtc.test(value) then Validated.valid(value.asInstanceOf[T]) else Validated.invalid(ops.rtc.message)

    /**
     * Refine the given value applicatively at runtime, resulting in a [[ValidatedNec]].
     *
     * @param constraint the constraint to test with the value to refine.
     * @return a [[Valid]] containing this value as [[IronType]] or an [[Invalid]] containing a [[NonEmptyChain]] of error messages.
     * @see [[validated]], [[validatedNel]].
     */
    def validatedNec(value: A): ValidatedNec[String, T] =
      if ops.rtc.test(value) then Validated.validNec(value.asInstanceOf[T]) else Validated.invalidNec(ops.rtc.message)

    /**
     * Refine the given value applicatively at runtime, resulting in a [[ValidatedNel]].
     *
     * @param constraint the constraint to test with the value to refine.
     * @return a [[Valid]] containing this value as [[IronType]] or an [[Invalid]] containing a [[NonEmptyList]] of error messages.
     * @see [[validated]], [[validatedNec]].
     */
    def validatedNel(value: A): ValidatedNel[String, T] =
      if ops.rtc.test(value) then Validated.validNel(value.asInstanceOf[T]) else Validated.invalidNel(ops.rtc.message)

    /**
     * Refine the given values applicatively at runtime, resulting in a [[EitherNec]].
     *
     * @param constraint the constraint to test with the value to refine.
     * @return a [[Right]] containing this value as [[IronType]] or an [[Left]] containing a [[NonEmptyChain]] of error messages.
     * @see [[eitherNec]], [[eitherAllNel]].
     */
    def eitherAllNec[F[_]](value: F[A])(using Traverse[F]): EitherNec[InvalidValue[A], F[T]] =
      ops.validatedAllNec(value).toEither

    /**
     * Refine the given values applicatively at runtime, resulting in a [[EitherNel]].
     *
     * @param constraint the constraint to test with the value to refine.
     * @return a [[Right]] containing this value as [[IronType]] or an [[Left]] containing a [[NonEmptyList]] of error messages.
     * @see [[eitherNel]], [[eitherAllNec]].
     */
    def eitherAllNel[F[_]](value: F[A])(using Traverse[F]): EitherNel[InvalidValue[A], F[T]] =
      ops.validatedAllNel(value).toEither

    /**
     * Refine the given values applicatively at runtime, resulting in a [[ValidatedNec]].
     *
     * @param constraint the constraint to test with the value to refine.
     * @return a [[Valid]] containing this value as [[IronType]] or an [[Invalid]] containing a [[NonEmptyChain]] of error messages.
     * @see [[validatedNec]], [[validatedAllNel]].
     */
    def validatedAllNec[F[_]](wrapper: F[A])(using traverse: Traverse[F]): ValidatedNec[InvalidValue[A], F[T]] =
      traverse.traverse(wrapper): value =>
        Validated.condNec[InvalidValue[A], T](ops.rtc.test(value), ops.assume(value), InvalidValue(value, ops.rtc.message))

    /**
     * Refine the given values applicatively at runtime, resulting in a [[ValidatedNel]].
     *
     * @param constraint the constraint to test with the value to refine.
     * @return a [[Valid]] containing this value as [[IronType]] or an [[Invalid]] containing a [[NonEmptyList]] of error messages.
     * @see [[validatedNel]], [[validatedAllNec]].
     */
    def validatedAllNel[F[_]](wrapper: F[A])(using traverse: Traverse[F]): ValidatedNel[InvalidValue[A], F[T]] =
      traverse.traverse(wrapper): value =>
        Validated.condNel[InvalidValue[A], T](ops.rtc.test(value), ops.assume(value), InvalidValue(value, ops.rtc.message))

