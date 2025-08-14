package io.github.iltotore.iron

import zio.json.{JsonDecoder, JsonEncoder}

object zioJson extends ZioJsonLowPriority:
  given [T](using mirror: RefinedType.Mirror[T], ev: JsonDecoder[mirror.IronType]): JsonDecoder[T] =
    ev.asInstanceOf[JsonDecoder[T]]

  given [T](using mirror: RefinedType.Mirror[T], ev: JsonEncoder[mirror.IronType]): JsonEncoder[T] =
    ev.asInstanceOf[JsonEncoder[T]]

  given [T](using mirror: RefinedSubtype.Mirror[T], ev: JsonDecoder[mirror.IronType]): JsonDecoder[T] =
    ev.asInstanceOf[JsonDecoder[T]]

  given [T](using mirror: RefinedSubtype.Mirror[T], ev: JsonEncoder[mirror.IronType]): JsonEncoder[T] =
    ev.asInstanceOf[JsonEncoder[T]]

private trait ZioJsonLowPriority:
  given [A, C](using decoder: JsonDecoder[A], constraint: RuntimeConstraint[A, C]): JsonDecoder[A :| C] =
    decoder.mapOrFail(_.refineEither)

  given [A, C](using encoder: JsonEncoder[A]): JsonEncoder[A :| C] =
    encoder.asInstanceOf[JsonEncoder[A :| C]]
