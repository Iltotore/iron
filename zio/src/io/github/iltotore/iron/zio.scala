package io.github.iltotore.iron

import _root_.zio.NonEmptyChunk
import _root_.zio.prelude.{Debug, Equal, Hash, Ord, PartialOrd, Validation}

object zio extends IronZIOInstances:

  extension [A](value: A)

    /**
     * Refine the given value applicatively at runtime, resulting in a [[Validation]].
     *
     * @param constraint the constraint to test with the value to refine.
     * @return a [[Valid]] containing this value as [[IronType]] or an [[Validation.Failure]] containing a [[NonEmptyChunk]] of error messages.
     */
    inline def refineValidation[C](using inline constraint: Constraint[A, C]): Validation[String, A :| C] =
      Validation.fromPredicateWith(constraint.message)(value.asInstanceOf[A :| C])(constraint.test(_))

trait IronZIOInstances extends IronZIOLowPriority:

  inline given [A, C](using ev: Debug[A]): Debug[A :| C] = ev.asInstanceOf[Debug[A :| C]]
  inline given [A, C](using ev: Equal[A]): Equal[A :| C] = ev.asInstanceOf[Equal[A :| C]]
  inline given [A, C](using ev: PartialOrd[A]): PartialOrd[A :| C] = ev.asInstanceOf[PartialOrd[A :| C]]
  inline given [A, C](using ev: Ord[A]): Ord[A :| C] = ev.asInstanceOf[Ord[A :| C]]


trait IronZIOLowPriority:

  inline given [A, C](using ev: Hash[A]): Hash[A :| C] = ev.asInstanceOf[Hash[A :| C]]
