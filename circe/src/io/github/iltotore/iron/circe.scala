package io.github.iltotore.iron

import io.circe.*

/**
 * Implicit [[Encoder]]s and [[Decoder]]s for refined types.
 */
object circe extends CirceLowPriority:

  given [T](using mirror: RefinedType.Mirror[T], ev: Decoder[mirror.IronType]): Decoder[T] =
    ev.asInstanceOf[Decoder[T]]

  given [T](using mirror: RefinedType.Mirror[T], ev: Encoder[mirror.IronType]): Encoder[T] =
    ev.asInstanceOf[Encoder[T]]


  given [T](using mirror: RefinedType.Mirror[T], ev: KeyDecoder[mirror.IronType]): KeyDecoder[T] =
    ev.asInstanceOf[KeyDecoder[T]]

  given [T](using mirror: RefinedType.Mirror[T], ev: KeyEncoder[mirror.IronType]): KeyEncoder[T] =
    ev.asInstanceOf[KeyEncoder[T]]

private trait CirceLowPriority:
  /**
   * A [[Decoder]] for refined types. Decodes to the underlying type then checks the constraint.
   *
   * @param decoder    the [[Decoder]] of the underlying type.
   * @param constraint the [[RuntimeConstraint]] implementation to test the decoded value.
   */
  given [A, B](using decoder: Decoder[A], constraint: RuntimeConstraint[A, B]): Decoder[A :| B] =
    decoder.emap(_.refineEither)


  /**
   * An [[Encoder]] instance for refined types. Basically the underlying type [[Encoder]].
   *
   * @param encoder the [[Encoder]] of the underlying type.
   */
  given [A, B](using encoder: Encoder[A]): Encoder[A :| B] = encoder.asInstanceOf[Encoder[A :| B]]

  /**
   * A [[KeyDecoder]] for refined types. Decodes to the underlying type then checks the constraint.
   *
   * @param decoder    the [[KeyDecoder]] of the underlying type.
   * @param constraint the [[RuntimeConstraint]] implementation to test the decoded value.
   */
  given [A, B](using decoder: KeyDecoder[A], constraint: RuntimeConstraint[A, B]): KeyDecoder[A :| B] =
    KeyDecoder.instance: input =>
      decoder.apply(input).flatMap[A :| B](_.refineOption)


  /**
   * An [[KeyEncoder]] instance for refined types. Basically the underlying type [[KeyEncoder]].
   *
   * @param encoder the [[KeyEncoder]] of the underlying type.
   */
  given [A, B](using encoder: KeyEncoder[A]): KeyEncoder[A :| B] = encoder.asInstanceOf[KeyEncoder[A :| B]]
