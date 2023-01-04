package io.github.iltotore.iron

import _root_.zio.NonEmptyChunk
import _root_.zio.prelude.{Debug, Equal, Hash, Ord, PartialOrd, Validation}

object zio:

  extension [A](value: A)

    /**
     * Refine the given value applicatively at runtime, resulting in a [[Validation]].
     *
     * @param constraint the constraint to test with the value to refine.
     * @return a [[Valid]] containing this value as [[IronType]] or an [[Validation.Failure]] containing a [[NonEmptyChunk]] of error messages.
     */
    inline def refineValidation[C](using inline constraint: Constraint[A, C]): Validation[String, A :| C] =
      Validation.fromPredicateWith(constraint.message)(value.asInstanceOf[A :| C])(constraint.test(_))

