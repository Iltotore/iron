package io.github.iltotore.iron

import _root_.upickle.core.Abort
import _root_.upickle.default.*
import io.github.iltotore.iron.{:|, Constraint, RefinedType, refineEither}

/**
 * Implicit `Reader`s and `Writer`s for refined types using uPickle.
 */
object upickle extends UPickleLowPriority:

  /**
   * A uPickle `Reader` based on refined type mirrors.
   *
   * @param mirror the type mirror for refined types.
   * @param ev     the underlying `Reader` for the iron type.
   */
  given [T](using mirror: RefinedType.Mirror[T], ev: Reader[mirror.IronType]): Reader[T] =
    ev.asInstanceOf[Reader[T]]

  /**
   * A uPickle `Writer` based on refined type mirrors.
   *
   * @param mirror the type mirror for refined types.
   * @param ev     the underlying `Writer` for the iron type.
   */
  given [T](using mirror: RefinedType.Mirror[T], ev: Writer[mirror.IronType]): Writer[T] =
    ev.asInstanceOf[Writer[T]]

private trait UPickleLowPriority:

  /**
   * A `Reader` for refined types using uPickle. Decodes to the underlying type then checks the constraint.
   *
   * @param reader     the `Reader` of the underlying type.
   * @param constraint the `RuntimeConstraint` implementation to test the decoded value.
   */
  given [A, B](using reader: Reader[A], constraint: RuntimeConstraint[A, B]): Reader[A :| B] =
    reader.map(value =>
      value.refineEither match
        case Right(refinedValue) => refinedValue
        case Left(errorMessage) => throw Abort(errorMessage)
    )

  /**
   * A `Writer` instance for refined types using uPickle. This is essentially the underlying type `Writer`.
   *
   * @param writer the `Writer` of the underlying type.
   */
  given [A, B](using writer: Writer[A]): Writer[A :| B] = writer.asInstanceOf[Writer[A :| B]]
