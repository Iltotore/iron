package io.github.iltotore.iron

import cats.data.{NonEmptyChain, NonEmptyList, Validated, ValidatedNec, ValidatedNel}
import Validated.{Valid, Invalid}

/**
 * Utility methods for the Cats library.
 */
package object catsSupport:

  extension [A](value: A)

    /**
     * Refine the given value at runtime, resulting in a [[Validated]].
     *
     * @param constraint the constraint to test with the value to refine.
     * @return a [[Valid]] containing this value as [[IronType]] or an [[Invalid]] containing the constraint message.
     * @see [[refineNec]], [[refineNel]].
     */
    inline def refineValidated[B](using inline constraint: Constraint[A, B]): Validated[String, A :| B] =
      Validated.cond(constraint.test(value), value.asInstanceOf[A :| B], constraint.message)

    /**
     * Refine the given value applicatively at runtime, resulting in a [[ValidatedNec]].
     *
     * @param constraint the constraint to test with the value to refine.
     * @return a [[Valid]] containing this value as [[IronType]] or an [[Invalid]] containing a [[NonEmptyChain]] of error messages.
     * @see [[refineValidated]], [[refineNel]].
     */
    inline def refineNec[B](using inline constraint: Constraint[A, B]): ValidatedNec[String, A :| B] =
      Validated.condNec(constraint.test(value), value.asInstanceOf[A :| B], constraint.message)

    /**
     * Refine the given value applicatively at runtime, resulting in a [[ValidatedNel]].
     *
     * @param constraint the constraint to test with the value to refine.
     * @return a [[Valid]] containing this value as [[IronType]] or an [[Invalid]] containing a [[NonEmptyList]] of error messages.
     * @see [[refineValidated]], [[refineNec]].
     */
    inline def refineNel[B](using inline constraint: Constraint[A, B]): ValidatedNel[String, A :| B] =
      Validated.condNel(constraint.test(value), value.asInstanceOf[A :| B], constraint.message)
