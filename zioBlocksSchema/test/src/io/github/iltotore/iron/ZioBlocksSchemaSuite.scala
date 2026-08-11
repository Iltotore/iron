package io.github.iltotore.iron

import _root_.zio.blocks.schema.{DynamicSchema, DynamicValue, Modifier, PrimitiveValue, Schema, Validation}
import io.github.iltotore.iron.constraint.any.StrictEqual
import io.github.iltotore.iron.constraint.collection.{FixedLength, MaxLength, MinLength}
import io.github.iltotore.iron.constraint.numeric.{Positive, Positive0}
import io.github.iltotore.iron.constraint.string.Match
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
      assert(!summon[Schema[Altitude]].toDynamicSchema.conforms(DynamicValue.Primitive(PrimitiveValue.Double(-1.0))))

    test("supported numeric constraints are durable dynamic-schema validations"):
      val schema = summon[Schema[Int :| Positive]]
      val dynamicSchema = schema.toDynamicSchema
      val invalid = DynamicValue.Primitive(PrimitiveValue.Int(0))
      val persisted = DynamicSchema.toDynamicValue(dynamicSchema)
      val restored = DynamicSchema.fromDynamicValue(persisted)
      assert(!dynamicSchema.conforms(invalid))
      assert(!restored.conforms(invalid))
      assert(restored.modifiers.contains(Modifier.config("iron.validation.message", "Should be strictly positive")))

    test("non-negative constraints are translated"):
      val schema = summon[Schema[Int :| Positive0]]
      assert(!schema.toDynamicSchema.conforms(DynamicValue.Primitive(PrimitiveValue.Int(-1))))

    test("string patterns and length constraints are translated"):
      type ThreeToFiveCharacters = String :| (MinLength[3] & MaxLength[5])
      type ExactlyFourCharacters = String :| FixedLength[4]
      type Digits = String :| Match["[0-9]+"]

      assert(!summon[Schema[ThreeToFiveCharacters]].toDynamicSchema.conforms(DynamicValue.Primitive(PrimitiveValue.String("ab"))))
      assert(!summon[Schema[ExactlyFourCharacters]].toDynamicSchema.conforms(DynamicValue.Primitive(PrimitiveValue.String("abc"))))
      assert(!summon[Schema[Digits]].toDynamicSchema.conforms(DynamicValue.Primitive(PrimitiveValue.String("abc"))))

    test("constraint messages are retained as schema metadata"):
      val schema = summon[Schema[Int :| Positive]]
      assert(schema.reflect.modifiers.contains(Modifier.config("iron.validation.message", "Should be strictly positive")))

    test("the configuration can disable embedded validation and message metadata"):
      given ZioBlocksSchemaConfig = ZioBlocksSchemaConfig(
        validation = ZioBlocksSchemaConfig.ValidationEncoding.RuntimeOnly,
        messageMetadata = ZioBlocksSchemaConfig.MessageMetadata.Omit
      )
      val schema = summon[Schema[Int :| Positive0]]
      val invalid = DynamicValue.Primitive(PrimitiveValue.Int(-1))
      assert(schema.toDynamicSchema.conforms(invalid))
      assert(schema.reflect.modifiers.isEmpty)

    test("applications can translate custom constraints with a given"):
      type Answer = Int :| StrictEqual[42]
      given ZioBlocksValidation[Int, StrictEqual[42]] with
        def validation: Option[Validation[Int]] = Some(Validation.Numeric.Set(Set(42)))

      val schema = summon[Schema[Answer]]
      assert(!schema.toDynamicSchema.conforms(DynamicValue.Primitive(PrimitiveValue.Int(41))))
