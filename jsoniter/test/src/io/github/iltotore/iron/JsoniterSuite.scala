package io.github.iltotore.iron

import com.github.plokhotnyuk.jsoniter_scala.core.{JsonKeyCodec, JsonReader, JsonValueCodec, JsonWriter, readFromString, writeToString}
import com.github.plokhotnyuk.jsoniter_scala.macros.*
import io.github.iltotore.iron.constraint.all.{*, given}
import io.github.iltotore.iron.given
import io.github.iltotore.iron.jsoniter.given
import utest.{Show as _, *}

import scala.util.Try

object JsoniterSuite extends TestSuite:
  type Zero = StrictEqual[0]
  val zero: Int :| Zero = 0
  val one: Int :| Positive = 1

  case class Number(zero: Int :| Zero)

  given JsonValueCodec[Zero] = JsonCodecMaker.make
  given JsonValueCodec[Number] = JsonCodecMaker.make

  case class StatusData(userName: String)
  case class Status(statusList: List[StatusData] :| FixedLength[1])

  inline given codecMakerConfig: CodecMakerConfig = CodecMakerConfig.withFieldNameMapper(JsonCodecMaker.enforce_snake_case2)

  given JsonValueCodec[Status] = JsonCodecMaker.make(codecMakerConfig)

  given JsonKeyCodec[Int] = new:
    def decodeKey(in: JsonReader): Int = in.readKeyAsInt()
    def encodeKey(x: Int, out: JsonWriter): Unit = out.writeKey(x)

  given JsonValueCodec[Map[Int :| Positive, String]] = JsonCodecMaker.make

  extension [A](value: A)(using JsonValueCodec[A])
    def assertEncoding(expected: String): Unit = assert(writeToString(value) == expected)

  extension (value: String)
    def assertDecodingSuccess[A](expected: A)(using JsonValueCodec[A]): Unit = assert(Try(readFromString[A](value)).fold(_ => false, _ == expected))
    def assertDecodingFailure[A](using JsonValueCodec[A]): Unit = assert(Try(readFromString[A](value)).isFailure)

  val tests: Tests = Tests:
    test("encoding"):
      test - zero.assertEncoding("0")
      test - Number(0).assertEncoding("""{"zero":0}""")
      test - Status(List(StatusData("John"))).assertEncoding("""{"status_list":[{"user_name":"John"}]}""")
      test - Map(one -> "one").assertEncoding("""{"1":"one"}""")

    test("decoding - valid predicate"):
      test - "0".assertDecodingSuccess[Int :| Zero](zero)
      test - """{"zero":0}""".assertDecodingSuccess[Number](Number(0))
      test - """{"1":"one"}""".assertDecodingSuccess[Map[Int :| Positive, String]](Map(one -> "one"))

    test("decoding - invalid predicate"):
      test - "1".assertDecodingFailure[Zero]
      test - """{"zero":1}""".assertDecodingFailure[Number]
      test - """{"0":"zero"}""".assertDecodingFailure[Map[Int :| Positive, String]]
