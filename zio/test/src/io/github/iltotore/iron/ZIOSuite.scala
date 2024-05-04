package io.github.iltotore.iron

import io.github.iltotore.iron.constraint.all.*
import utest.{Show as _, *}

import scala.runtime.stdLibPatches.Predef.assert
import io.github.iltotore.iron.zio.{*, given}
import _root_.zio.prelude.ZValidation
import _root_.zio.Chunk
import _root_.zio.NonEmptyChunk

object ZIOSuite extends TestSuite:
  val tests: Tests = Tests:

    test("ZIO validation"):
      assert(Temperature.validation(2) == ZValidation.Success[String, Temperature](Chunk.empty, Temperature(2)))

      assert(
        Temperature.validation(0) ==
          ZValidation.Failure[String, String](Chunk.empty, NonEmptyChunk.single("Should be strictly positive"))
      )

    test("all"):
      test("mapLogicToCovariant"):
        test - assert(Temperature.optionAll(NonEmptyChunk(1, 2, 3)).contains(NonEmptyChunk(Temperature(1), Temperature(2), Temperature(3))))
        test - assert(Temperature.optionAll(NonEmptyChunk(1, 2, -3)).isEmpty)

      val valid = List(1, 2, 3)
      val invalid = List(1, -2, -3)

      test("validation"):
        test - assert(valid.refineAllValidation[Positive] == ZValidation.Success(Chunk.empty, Chunk.from(valid)))
        test - assert(invalid.refineAllValidation[Positive] == ZValidation.Failure(Chunk.empty, NonEmptyChunk(
          InvalidValue(-2, "Should be strictly positive"),
          InvalidValue(-3, "Should be strictly positive")
        )))
        
      test("newtype"):
        test - assert(Temperature.validationAll(valid) == ZValidation.Success(Chunk.empty, Chunk.from(valid)))
        test - assert(Temperature.validationAll(invalid) == ZValidation.Failure(Chunk.empty, NonEmptyChunk(
          InvalidValue(-2, "Should be strictly positive"),
          InvalidValue(-3, "Should be strictly positive")
        )))
        
      test("furtherValidation"):
        val furtherValid = List(2, 4, 6).refineAllUnsafe[Positive]
        val furtherInvalid = List(1, 2, 3).refineAllUnsafe[Positive]
        
        test - assert(furtherValid.refineAllFurtherValidation[Even] == ZValidation.Success(Chunk.empty, Chunk.from(furtherValid)))
        test - assert(furtherInvalid.refineAllFurtherValidation[Even] == ZValidation.Failure(Chunk.empty, NonEmptyChunk(
          InvalidValue(1, "Should be a multiple of 2"),
          InvalidValue(3, "Should be a multiple of 2")
        )))