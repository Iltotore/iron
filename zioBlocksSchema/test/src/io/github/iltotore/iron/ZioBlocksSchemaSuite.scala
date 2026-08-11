package io.github.iltotore.iron

import _root_.zio.blocks.schema.Schema
import io.github.iltotore.iron.constraint.numeric.Positive
import zioBlocksSchema.given
import utest.*

object ZioBlocksSchemaSuite extends TestSuite:

  val tests: Tests = Tests:

    test("schemas are resolved for iron types"):
      summon[Schema[Int :| Positive]]
      summon[Schema[Temperature]]
      summon[Schema[Altitude]]

    test("schemas round-trip valid refined values"):
      val schema = summon[Schema[Int :| Positive]]
      assert(schema.fromDynamicValue(schema.toDynamicValue(1)) == Right(1))

    test("schemas validate refined values when decoding"):
      val schema = summon[Schema[Int :| Positive]]
      assert(schema.fromDynamicValue(Schema[Int].toDynamicValue(0)).isLeft)

    test("schemas round-trip newtypes and subtypes"):
      val temperature = Temperature(20.0)
      val altitude = Altitude(100.0)
      val temperatureSchema = summon[Schema[Temperature]]
      val altitudeSchema = summon[Schema[Altitude]]
      assert(temperatureSchema.fromDynamicValue(temperatureSchema.toDynamicValue(temperature)) == Right(temperature))
      assert(altitudeSchema.fromDynamicValue(altitudeSchema.toDynamicValue(altitude)) == Right(altitude))

    test("newtype schemas retain their constraints"):
      val schema = summon[Schema[Temperature]]
      assert(schema.fromDynamicValue(Schema[Double].toDynamicValue(-1.0)).isLeft)
