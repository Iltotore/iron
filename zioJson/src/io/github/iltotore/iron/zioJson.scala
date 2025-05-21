package io.github.iltotore.iron

import io.github.iltotore.iron.internal.NotNothing
import zio.json.{JsonDecoder, JsonEncoder}

object zioJson:

  inline given [A: NotNothing, C](using inline decoder: JsonDecoder[A], constraint: Constraint[A, C]): JsonDecoder[A :| C] =
    decoder.mapOrFail(_.refineEither)

  inline given [A: NotNothing, C](using inline encoder: JsonEncoder[A]): JsonEncoder[A :| C] =
    encoder.asInstanceOf[JsonEncoder[A :| C]]

  inline given [T](using mirror: RefinedType.Mirror[T], ev: JsonDecoder[mirror.IronType]): JsonDecoder[T] =
    ev.asInstanceOf[JsonDecoder[T]]

  inline given [T](using mirror: RefinedType.Mirror[T], ev: JsonEncoder[mirror.IronType]): JsonEncoder[T] =
    ev.asInstanceOf[JsonEncoder[T]]
