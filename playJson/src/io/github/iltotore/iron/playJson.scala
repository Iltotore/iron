package io.github.iltotore.iron

import play.api.libs.json.{Reads, Writes, JsSuccess, JsError}

/**
 * Implicit [[Writes]]s and [[Reads]]s for refined types.
 */
object playJson extends PlayJsonLowPriority:

  given [T](using mirror: RefinedType.Mirror[T], ev: Reads[mirror.IronType]): Reads[T] =
    ev.asInstanceOf[Reads[T]]

  given [T](using mirror: RefinedType.Mirror[T], ev: Writes[mirror.IronType]): Writes[T] =
    ev.asInstanceOf[Writes[T]]

  given [T](using mirror: RefinedSubtype.Mirror[T], ev: Reads[mirror.IronType]): Reads[T] =
    ev.asInstanceOf[Reads[T]]

  given [T](using mirror: RefinedSubtype.Mirror[T], ev: Writes[mirror.IronType]): Writes[T] =
    ev.asInstanceOf[Writes[T]]

private trait PlayJsonLowPriority:
  /**
   * A [[Reads]] for refined types. Decodes to the underlying type then checks the constraint.
   *
   * @param reads      the [[Reads]] of the underlying type.
   * @param constraint the [[RuntimeConstraint]] implementation to test the decoded value.
   */
  given [A, B](using reads: Reads[A], constraint: RuntimeConstraint[A, B]): Reads[A :| B] =
    reads.flatMapResult: a =>
      a.refineEither[B] match
        case Left(error) => JsError(error)
        case Right(b)    => JsSuccess(b)

  /**
   * A [[Writes]] instance for refined types. Basically the underlying type [[Writes]].
   *
   * @param writes the [[Writes]] of the underlying type.
   */
  given [A, B](using writes: Writes[A]): Writes[A :| B] = writes.asInstanceOf[Writes[A :| B]]
