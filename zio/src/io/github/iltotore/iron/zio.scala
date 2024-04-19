package io.github.iltotore.iron

import _root_.zio.NonEmptyChunk
import _root_.zio.prelude.{Covariant, Debug, Equal, Hash, Ord, Validation}

object zio extends RefinedTypeOpsZio:

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

  extension [A, C, T](ops: RefinedTypeOps[A, C, T])
    /**
     * Refine the given value applicatively at runtime, resulting in a [[Validation]].
     *
     * @param constraint the constraint to test with the value to refine.
     * @return a [[Valid]] containing this value as [[T]] or an [[Validation.Failure]] containing a [[NonEmptyChunk]] of error messages.
     */
    def validation(value: A): Validation[String, T] =
      Validation.fromPredicateWith(ops.rtc.message)(value)(ops.rtc.test(_)).asInstanceOf[Validation[String, T]]

  given [F[+_]](using covariant: Covariant[F]): MapLogic[F] with

    override def map[A, B](wrapper: F[A], f: A => B): F[B] = covariant.map(f)(wrapper)

private trait RefinedTypeOpsZio extends RefinedTypeOpsZioLowPriority:

  inline given [T](using mirror: RefinedTypeOps.Mirror[T], ev: Debug[mirror.IronType]): Debug[T] = ev.asInstanceOf[Debug[T]]

  inline given [T](using mirror: RefinedTypeOps.Mirror[T], ev: Equal[mirror.IronType]): Equal[T] = ev.asInstanceOf[Equal[T]]

  inline given [T](using mirror: RefinedTypeOps.Mirror[T], ev: Ord[mirror.IronType]): Ord[T] = ev.asInstanceOf[Ord[T]]

private trait RefinedTypeOpsZioLowPriority:

  inline given [T](using mirror: RefinedTypeOps.Mirror[T], ev: Hash[mirror.IronType]): Hash[T] = ev.asInstanceOf[Hash[T]]
