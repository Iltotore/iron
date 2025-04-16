package io.github.iltotore.iron

import _root_.upickle.core.Abort
import _root_.upickle.default.*
import io.github.iltotore.iron.{:|, refineEither}

/**
 * Implicit `Reader`s and `Writer`s for refined types using uPickle.
 */
object upickle extends UPickleLowPrio:
  export RefinedType.Compat.given
private trait UPickleLowPrio:

  /**
   * A `Reader` for refined types using uPickle. Decodes to the underlying type then checks the constraint.
   *
   * @param reader the `Reader` of the underlying type.
   * @param constraint the `Constraint` implementation to test the decoded value.
   */
  inline given [A, B](using inline reader: Reader[A], inline constraint: Constraint[A, B]): Reader[A :| B] =
    reader.map(value =>
      value.refineEither match
        case Right(refinedValue) => refinedValue
        case Left(errorMessage)  => throw Abort(errorMessage)
    )

  /**
   * A `Writer` instance for refined types using uPickle. This is essentially the underlying type `Writer`.
   *
   * @param writer the `Writer` of the underlying type.
   */
  inline given [A, B](using inline writer: Writer[A]): Writer[A :| B] = writer.asInstanceOf[Writer[A :| B]]
