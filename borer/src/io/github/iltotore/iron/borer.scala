package io.github.iltotore.iron

import io.bullet.borer.{Decoder, Encoder}

/**
 * Automatic construction of borer [[Encoder]] and [[Decoder]] instances for refined types.
 */
object borer extends BorerLowPriority:

  given [T](using m: RefinedType.Mirror[T], ev: Encoder[m.IronType]): Encoder[T] =
    ev.asInstanceOf[Encoder[T]]

  given [T](using m: RefinedType.Mirror[T], ev: Decoder[m.IronType]): Decoder[T] =
    ev.asInstanceOf[Decoder[T]]

private trait BorerLowPriority:
  given [A, B](using encoder: Encoder[A]): Encoder[A :| B] =
    encoder.asInstanceOf[Encoder[A :| B]]

  given [A, B](using decoder: Decoder[A], constraint: RuntimeConstraint[A, B]): Decoder[A :| B] =
    r =>
      decoder.read(r).refineEither match
        case Left(msg) => r.validationFailure(msg)
        case Right(x) => x


