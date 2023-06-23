package io.github.iltotore.iron

import io.circe.{Decoder, DecodingFailure, Encoder, HCursor}

/**
 * Implicit [[Encoder]]s and [[Decoder]]s for refined types.
 */
object circe:

  /**
   * A [[Decoder]] for refined types. Decodes to the underlying type then checks the constraint.
   *
   * @param decoder the [[Decoder]] of the underlying type.
   * @param constraint the [[Constraint]] implementation to test the decoded value.
   */
  inline given [A, B](using inline decoder: Decoder[A], inline constraint: Constraint[A, B]): Decoder[A :| B] =
    decoder.emap(_.refineEither)

  /**
   * An [[Encoder]] instance for refined types. Basically the underlying type [[Encoder]].
   *
   * @param encoder the [[Encoder]] of the underlying type.
   */
  inline given [A, B](using inline encoder: Encoder[A]): Encoder[A :| B] = encoder.asInstanceOf[Encoder[A :| B]]

  inline given[T](using inline mirror: RefinedTypeOps.Mirror[T], ev: Decoder[mirror.IronType]): Decoder[T] =
    ev.asInstanceOf[Decoder[T]]

  inline given[T](using inline mirror: RefinedTypeOps.Mirror[T], ev: Encoder[mirror.IronType]): Encoder[T] =
    ev.asInstanceOf[Encoder[T]]
