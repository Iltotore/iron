package io.github.iltotore.iron

import com.github.plokhotnyuk.jsoniter_scala.core.{JsonValueCodec, readFromString, writeToString}
import com.github.plokhotnyuk.jsoniter_scala.macros.*
import io.github.iltotore.iron.{*, given}
import io.github.iltotore.iron.jsoniter.given
import io.github.iltotore.iron.constraint.all.{*, given}
import utest.{Show as _, *}

import scala.runtime.stdLibPatches.Predef.assert
import scala.util.Try

object JsoniterSuite extends TestSuite:
  type Zero = StrictEqual[0]
  val zero: Int :| Zero = 0

  case class Number(zero: Int :| Zero)

  given JsonValueCodec[Zero] = JsonCodecMaker.make
  given JsonValueCodec[Number] = JsonCodecMaker.make

  extension [A](value: A)(using JsonValueCodec[A])
    def assertEncoding(expected: String): Unit = assert(writeToString(value) == expected)

  extension (value: String)
    def assertDecodingSuccess[A](expected: A)(using JsonValueCodec[A]): Unit = assert(Try(readFromString[A](value)).fold(_ => false, _ == expected))
    def assertDecodingFailure[A](using JsonValueCodec[A]): Unit = assert(Try(readFromString[A](value)).isFailure)

  val tests: Tests = Tests:
    test("encoding"):
      test - zero.assertEncoding("0")
      test - Number(0).assertEncoding("""{"zero":0}""")

    test("decoding - valid predicate"):
      test - "0".assertDecodingSuccess[Int :| Zero](zero)
      test - """{"zero":0}""".assertDecodingSuccess[Number](Number(0))

    test("decoding - invalid predicate"):
      test - "1".assertDecodingFailure[Zero]
      test - """{"zero":1}""".assertDecodingFailure[Number]

    test("RefinedType givens get resolved"):
      summon[JsonValueCodec[Temperature.T]]
      summon[JsonValueCodec[EmptySeqDouble.T]]
