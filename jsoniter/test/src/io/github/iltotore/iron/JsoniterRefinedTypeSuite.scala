package io.github.iltotore.iron

import com.github.plokhotnyuk.jsoniter_scala.core.{JsonValueCodec, readFromString, writeToString}
import com.github.plokhotnyuk.jsoniter_scala.macros.*
import io.github.iltotore.iron
import io.github.iltotore.iron.{*, given}
import io.github.iltotore.iron.jsoniter.given
import io.github.iltotore.iron.constraint.all.{*, given}
import utest.{Show as _, *}

import scala.runtime.stdLibPatches.Predef.assert
import scala.util.Try

object JsoniterRefinedTypeSuite extends TestSuite:
  val temperature: Temperature = Temperature(10)

  case class TemperatureNumber(temperature: Temperature)

  given JsonValueCodec[TemperatureNumber] = JsonCodecMaker.make

  case class Status(statusList: List[TemperatureNumber] :| FixedLength[1])

  inline given codecMakerConfig: CodecMakerConfig = CodecMakerConfig.withFieldNameMapper(JsonCodecMaker.enforce_snake_case2)

  given JsonValueCodec[Status] = JsonCodecMaker.make(codecMakerConfig)

  extension [A](value: A)(using JsonValueCodec[A])
    def assertEncoding(expected: String): Unit = assert(writeToString(value) == expected)

  extension (value: String)
    def assertDecodingSuccess[A](expected: A)(using JsonValueCodec[A]): Unit = assert(Try(readFromString[A](value)).fold(_ => false, _ == expected))
    def assertDecodingFailure[A](using JsonValueCodec[A]): Unit = assert(Try(readFromString[A](value)).isFailure)

  val tests: Tests = Tests:
    test("encoding"):
      test - temperature.assertEncoding("10.0")
      test - TemperatureNumber(Temperature(20)).assertEncoding("""{"temperature":20.0}""")
      test - Status(List(TemperatureNumber(Temperature(30)))).assertEncoding("""{"status_list":[{"temperature":30.0}]}""")

    test("decoding - valid predicate"):
      test - "10.0".assertDecodingSuccess[Temperature](temperature)
      test - """{"temperature":20.0}""".assertDecodingSuccess[TemperatureNumber](TemperatureNumber(Temperature(20)))

    test("decoding - invalid predicate"):
      test - "-10.0".assertDecodingFailure[Temperature]
      test - """{"temperature":-20.0}""".assertDecodingFailure[TemperatureNumber]
