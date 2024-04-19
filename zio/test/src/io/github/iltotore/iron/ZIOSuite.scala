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
      assert(Temperature.validation(2.0) == ZValidation.Success[String, Temperature](Chunk.empty, Temperature(2.0)))

      assert(
        Temperature.validation(0.0) ==
          ZValidation.Failure[String, String](Chunk.empty, NonEmptyChunk.single("Should be strictly positive"))
      )

    test("refineAll"):
      test - assert(Temperature.optionAll(NonEmptyChunk(1, 2, 3)).contains(NonEmptyChunk(Temperature(1), Temperature(2), Temperature(3))))
      test - assert(Temperature.optionAll(NonEmptyChunk(1, 2, -3)).isEmpty)
