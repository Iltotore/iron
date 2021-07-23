package io.github.iltotore.iron

import io.circe.Decoder
import io.github.iltotore.iron.constraint.{Constraint, refineValue}

package object circe {

  inline given[A, B, C <: Constraint[A, B]](using inputDecoder: Decoder[A], c: C): Decoder[A ==> B] = inputDecoder
    .map(refineValue[A, B, C](_))
}