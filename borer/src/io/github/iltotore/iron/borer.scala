package io.github.iltotore.iron

import io.bullet.borer.{Decoder, Encoder}

/**
 * Automatic construction of borer [[Encoder]] and [[Decoder]] instances for refined types.
 */
object borer extends BorerLowPrio:
  export RefinedType.Compat.given
private trait BorerLowPrio:

  inline given [A, B](using inline encoder: Encoder[A]): Encoder[A :| B] =
    encoder.asInstanceOf[Encoder[A :| B]]

  inline given [A, B](using inline decoder: Decoder[A], inline constraint: Constraint[A, B]): Decoder[A :| B] =
    Decoder: r =>
      decoder.read(r).refineEither match
        case Left(msg) => r.validationFailure(msg)
        case Right(x)  => x
