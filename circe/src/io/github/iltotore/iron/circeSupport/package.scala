package io.github.iltotore.iron

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.deriveEncoder
import io.github.iltotore.iron.constraint.{Constraint, IllegalValueError, refineValue}

package object circeSupport {

  inline given[A, B, C <: Constraint[A, B]](using inputDecoder: Decoder[A], c: C): Decoder[A ==> B] = inputDecoder
    .map(refineValue[A, B, C](_))

  inline given [A](using Encoder[A]): Encoder[IllegalValueError[A]] = deriveEncoder

  inline given Encoder[IllegalValueError.Field] = deriveEncoder

}