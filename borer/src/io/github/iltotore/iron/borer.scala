package io.github.iltotore.iron

import io.bullet.borer.{Encoder, Decoder}
import io.github.iltotore.iron.internal.NotNothing

/**
 * Automatic construction of borer [[Encoder]] and [[Decoder]] instances for refined types.
 */
object borer:

  inline given [A: NotNothing, B](using inline encoder: Encoder[A]): Encoder[A :| B] =
    encoder.asInstanceOf[Encoder[A :| B]]

  inline given [A: NotNothing, B](using inline decoder: Decoder[A], inline constraint: Constraint[A, B]): Decoder[A :| B] =
    Decoder: r =>
      decoder.read(r).refineEither match
        case Left(msg) => r.validationFailure(msg)
        case Right(x)  => x

  inline given [T](using m: RefinedType.Mirror[T], ev: Encoder[m.IronType]): Encoder[T] =
    ev.asInstanceOf[Encoder[T]]

  inline given [T](using m: RefinedType.Mirror[T], ev: Decoder[m.IronType]): Decoder[T] =
    ev.asInstanceOf[Decoder[T]]
