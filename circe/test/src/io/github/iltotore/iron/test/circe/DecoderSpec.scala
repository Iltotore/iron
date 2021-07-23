package io.github.iltotore.iron.test.circe

import io.github.iltotore.iron.*, constraint.{given, *}, circe.{given}, test.{given, *}
import io.circe.parser.*

class DecoderSpec extends UnitSpec {

  "A Decoder[A ==> B]" should "decode A into A ==> B and evaluate the assertion" in {
    assert(decode[Boolean ==> Dummy]("true").exists(_.isRight))
    assert(decode[Boolean ==> Dummy]("false").exists(_.isLeft))
    assert(decode[Boolean ==> Dummy]("???").isLeft)
  }
}