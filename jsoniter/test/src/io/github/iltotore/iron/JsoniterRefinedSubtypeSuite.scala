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

object JsoniterRefinedSubtypeSuite extends TestSuite:
  val altitude: Altitude = Altitude(10)

  case class AltitudeNumber(altitude: Altitude)

  given JsonValueCodec[AltitudeNumber] = JsonCodecMaker.make

  case class Status(statusList: List[AltitudeNumber] :| FixedLength[1])

  inline given codecMakerConfig: CodecMakerConfig = CodecMakerConfig.withFieldNameMapper(JsonCodecMaker.enforce_snake_case2)

  given JsonValueCodec[Status] = JsonCodecMaker.make(codecMakerConfig)

  extension [A](value: A)(using JsonValueCodec[A])
    def assertEncoding(expected: String): Unit = assert(writeToString(value) == expected)

  extension (value: String)
    def assertDecodingSuccess[A](expected: A)(using JsonValueCodec[A]): Unit = assert(Try(readFromString[A](value)).fold(_ => false, _ == expected))
    def assertDecodingFailure[A](using JsonValueCodec[A]): Unit = assert(Try(readFromString[A](value)).isFailure)

  val tests: Tests = Tests:
    test("encoding"):
      test - altitude.assertEncoding("10.0")
      test - AltitudeNumber(Altitude(20)).assertEncoding("""{"altitude":20.0}""")
      test - Status(List(AltitudeNumber(Altitude(30)))).assertEncoding("""{"status_list":[{"altitude":30.0}]}""")

    test("decoding - valid predicate"):
      test - "10.0".assertDecodingSuccess[Altitude](altitude)
      test - """{"altitude":20.0}""".assertDecodingSuccess[AltitudeNumber](AltitudeNumber(Altitude(20)))

    test("decoding - invalid predicate"):
      test - "-10.0".assertDecodingFailure[Altitude]
      test - """{"altitude":-20.0}""".assertDecodingFailure[AltitudeNumber]
