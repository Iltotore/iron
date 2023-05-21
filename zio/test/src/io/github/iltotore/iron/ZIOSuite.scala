package io.github.iltotore.iron

import io.github.iltotore.iron.constraint.all.*
import utest.{Show as _, *}

import scala.runtime.stdLibPatches.Predef.assert
import io.github.iltotore.iron.zio as ironZio
import _root_.zio.prelude.ZValidation
import _root_.zio.Chunk
import _root_.zio.NonEmptyChunk

opaque type Temperature = Double :| Positive
object Temperature extends RefinedTypeOps[Temperature]

import io.github.iltotore.iron.zio.validation

object ZIOSuite extends TestSuite:
  val tests: Tests = Tests {

    test("ZIO validation") {
      assert(Temperature.validation(2.0) == ZValidation.Success[String, Temperature](Chunk.empty, Temperature(2.0)))

      assert(
        Temperature.validation(0.0) ==
          ZValidation.Failure[String, String](Chunk.empty, NonEmptyChunk.single("Should be strictly positive"))
      )
    }
  }
