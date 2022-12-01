package io.github.iltotore.iron

import zio.json.{JsonDecoder, JsonEncoder}

object zioJson:

  inline given [A, C](using inline decoder: JsonDecoder[A], constraint: Constraint[A, C]): JsonDecoder[A :| C] =
    decoder.mapOrFail(_.refineEither)

  inline given [A, C](using inline encoder: JsonEncoder[A]): JsonEncoder[A :| C] =
    encoder.asInstanceOf[JsonEncoder[A :| C]]
