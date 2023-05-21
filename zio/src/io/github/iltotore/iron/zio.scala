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


  extension [A, C1](value: A :| C1)

    /**
     * Refine the given value again applicatively at runtime, resulting in a [[Validation]].
     *
     * @param constraint the new constraint to test.
     * @return a [[Valid]] containing this value as [[IronType]] or an [[Validation.Failure]] containing a [[NonEmptyChunk]] of error messages.
     */
    inline def refineFurtherValidation[C2](using inline constraint: Constraint[A, C2]): Validation[String, A :| (C1 & C2)] =
      (value: A).refineValidation[C2].map(_.assumeFurther[C1])

  extension [A, C, T](ops: RefinedTypeOpsImpl[A, C, T])
    /**
     * Refine the given value applicatively at runtime, resulting in a [[Validation]].
     *
     * @param constraint the constraint to test with the value to refine.
     * @return a [[Valid]] containing this value as [[T]] or an [[Validation.Failure]] containing a [[NonEmptyChunk]] of error messages.
     */
    inline def validation(value: A)(using inline constraint: Constraint[A, C]): Validation[String, T] =
      value.refineValidation[C].map(_.asInstanceOf[T])
