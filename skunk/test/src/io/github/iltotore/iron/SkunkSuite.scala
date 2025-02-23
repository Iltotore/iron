package io.github.iltotore.iron

import _root_.skunk.*
import _root_.skunk.implicits.*
import _root_.skunk.codec.all.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.skunk.*
import io.github.iltotore.iron.skunk.given
import utest.*

object SkunkSuite extends TestSuite:

  given Codec[Int] = int4

  type PositiveInt = PositiveInt.T
  object PositiveInt extends RefinedTypeOps[Int, Positive]:
    given Codec[PositiveInt] = summon[Codec[Int]].imap(applyUnsafe)(_.value)

  val tests: Tests = Tests {

    test("codec"):
      test("ironType"):
        test("success") - assert(summon[Codec[Int :| Positive]].decode(0, List(Some("5"))) == Right(5))
        test("failure") - assert(summon[Codec[Int :| Positive]].decode(0, List(Some("-5"))).isLeft)
        test("success") - assert(summon[Codec[Int :| Positive]].encode(5) == List(Some("5")))

      test("newType"):
        test("success") - assert(summon[Codec[PositiveInt]].decode(0, List(Some("5"))) == Right(PositiveInt(5)))
        test("failure") - assert(summon[Codec[PositiveInt]].decode(0, List(Some("-5"))).isLeft)
        test("success") - assert(summon[Codec[PositiveInt]].encode(PositiveInt(5)) == List(Some("5")))

    test("encoder"):
      test("ironType"):
        test("success") - assert(summon[Encoder[Int :| Positive]].encode(5) == List(Some("5")))

      test("newType"):
        test("success") - assert(summon[Encoder[PositiveInt]].encode(PositiveInt(5)) == List(Some("5")))

    test("decoder"):
      test("ironType"):
        test("success") - assert(summon[Decoder[Int :| Positive]].decode(0, List(Some("5"))) == Right(PositiveInt(5)))
        test("failure") - assert(summon[Decoder[Int :| Positive]].decode(0, List(Some("-5"))).isLeft)

      test("newType"):
        test("success") - assert(summon[Decoder[PositiveInt]].decode(0, List(Some("5"))) == Right(PositiveInt(5)))
        test("failure") - assert(summon[Decoder[PositiveInt]].decode(0, List(Some("-5"))).isLeft)
  }
