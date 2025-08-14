package io.github.iltotore.iron

import _root_.scodec.*
import _root_.scodec.bits.*

/**
 * Implicit [[Codec]]s for refined types.
 */
object scodec extends ScodecLowPriority:

  /**
   * Given instance for refined newtypes.
   * This enables newtypes to work with scodec's derives syntax.
   */
  given [T](using mirror: RefinedType.Mirror[T], ev: Codec[mirror.IronType]): Codec[T] =
    ev.asInstanceOf[Codec[T]]

  /**
   * Given instance for refined subtypes.
   * This enables refind subtypes to work with scodec's derives syntax.
   */
  given [T](using mirror: RefinedSubtype.Mirror[T], ev: Codec[mirror.IronType]): Codec[T] =
    ev.asInstanceOf[Codec[T]]

private trait ScodecLowPriority:
  /**
   * A [[Codec]] for refined types. Encodes from the underlying type and decodes to the underlying type then checks the constraint.
   *
   * @param codec      the [[Codec]] of the underlying type.
   * @param constraint the [[RuntimeConstraint]] implementation to test the decoded value.
   */
  given [A, B](using codec: Codec[A], constraint: RuntimeConstraint[A, B]): Codec[A :| B] =
    codec.exmap[A :| B](
      _.refineEither match
        case Right(value) => Attempt.successful(value)
        case Left(error)  => Attempt.failure(Err(error))
      ,
      value => Attempt.successful(value.asInstanceOf[A])
    )
