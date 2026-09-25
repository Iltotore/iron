package io.github.iltotore.iron

import utest.*
import io.scalaland.chimney.dsl.*
import io.github.iltotore.iron.constraint.numeric.Positive
import io.github.iltotore.iron.constraint.any.Pure
import chimney.given

object ChimneySuite extends TestSuite:
  val tests: Tests = Tests:
    final case class RawInt(i: Int)
    final case class PureInt(i: Int :| Pure)
    final case class PureIntNT(i: PureIntNewType)

    type PositiveIntNewType = PositiveIntNewType.T
    object PositiveIntNewType extends RefinedType[Int, Positive]

    type PureIntNewType = PureIntNewType.T
    object PureIntNewType extends RefinedType[Int, Pure]

    final case class PositiveInt(i: Int :| Positive)
    final case class PositiveIntNT(i: PositiveIntNewType)

    test("Successfull transformation from raw type to the one with pure constraint") - assert(RawInt(1).transformInto[PureInt].i == 1)
    test("Successfull transformation from raw type to the one with pure constraint (new type)") - assert(
      RawInt(1).transformInto[PureIntNT].i.value == 1
    )
    test("Successfull transformation from constrained type to raw") - assert(PositiveInt(1).transformInto[RawInt].i == 1)
    test("Successfull transformation from constrained type (new type) to raw") - assert(
      PositiveIntNT(PositiveIntNewType(1)).transformInto[RawInt].i == 1
    )

    test("Partial transformation from raw type to refined"):
      final case class From(i: Int)
      final case class To(i: Int :| Positive)

      test("pos") - assert(From(1).transformIntoPartial[To].asOption == Some(To(1)))
      test("neg") - assert(From(-1).transformIntoPartial[To].asErrorPathMessageStrings == List("i" -> "Should be strictly positive"))

    test("Partial transformation from raw type to new"):
      type PositiveInt = PositiveInt.T
      object PositiveInt extends RefinedType[Int, Positive]

      final case class From(i: Int)
      final case class To(i: PositiveInt)

      test("pos") - assert(From(1).transformIntoPartial[To].asOption == Some(To(PositiveInt(1))))
      test("neg") - assert(From(-1).transformIntoPartial[To].asErrorPathMessageStrings == List("i" -> "Should be strictly positive"))

    test("Avoid ambiguous givens (PartialTransformer is automatically derived from regular Transformer)"):
      type PureString = PureString.T
      object PureString extends RefinedType[String, Pure]

      final case class From(s: String)
      final case class To(s: PureString)

      final case class From1(s: String)
      final case class To1(s: String :| Pure)

      From("qwerty").transformIntoPartial[To]
      From1("qwerty").transformIntoPartial[To1]
