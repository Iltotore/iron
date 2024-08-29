package io.github.iltotore.iron

import _root_.cats.Show
import _root_.cats.kernel.*
import _root_.cats.derived.*
import _root_.cats.instances.all.*
import io.github.iltotore.iron.cats.{*, given}
import io.github.iltotore.iron.constraint.all.*
import utest.{Show as _, *}
import _root_.cats.data.Chain
import _root_.cats.data.NonEmptyChain
import _root_.cats.data.NonEmptyList
import _root_.cats.data.Validated.{Invalid, Valid}

import scala.runtime.stdLibPatches.Predef.assert

object CatsSuite extends TestSuite:

  type AgeR = DescribedAs[
    Greater[0] & Less[120],
    "Persons's age must be an integer between 1 and 120"
  ]

  type NameR = DescribedAs[
    Alphanumeric & MinLength[1] & MaxLength[50],
    "Person's name must be an alphanumeric of max length 50"
  ]

  case class Person(
      name: String :| NameR,
      surname: String :| Contain["z"],
      age: Int :| Greater[0]
  ) derives Eq, Order, Show

  given BoundedSemilattice[String] = BoundedSemilattice.instance("", _ + _)

  val tests: Tests = Tests {

    test("Cats instances are resolved for String iron types"):
      Eq[String :| NameR]
      Hash[String :| NameR]
      Order[String :| NameR]
      PartialOrder[String :| NameR]
      Show[String :| NameR]

    test("Cats instances are resolved for new types"):
      Eq[Temperature]
      Hash[Temperature]
      Order[Temperature]
      Show[Temperature]
      Eq[Moisture]
      Hash[Moisture]
      Order[Moisture]
      Show[Moisture]

    test("Cats instances are resolved for Int iron types"):
      Eq[Int :| AgeR]
      Hash[Int :| AgeR]
      UpperBounded[Int :| AgeR]
      Order[Int :| AgeR]
      PartialOrder[Int :| AgeR]
      Show[Int :| AgeR]
      LowerBounded[Int :| AgeR]

    test("Cats instances are resolved for a case class with iron types"):
      Eq[Person]
      Order[Person]
      Show[Person]

    test("alley") {
      test("commutativeMonoid") {
        test("int"):
          test("pos") - assert(CommutativeMonoid[Int :| Positive].combine(1, 5) == 6)
          test("neg") - assert(CommutativeMonoid[Int :| Negative].combine(-1, -5) == -6)

        test("long"):
          test("pos") - assert(CommutativeMonoid[Long :| Positive].combine(1, 5) == 6)
          test("neg") - assert(CommutativeMonoid[Long :| Negative].combine(-1, -5) == -6)

        test("float"):
          test("pos") - assert(CommutativeMonoid[Float :| Positive].combine(1, 5) == 6)
          test("neg") - assert(CommutativeMonoid[Float :| Negative].combine(-1, -5) == -6)

        test("double"):
          test("pos") - assert(CommutativeMonoid[Double :| Positive].combine(1, 5) == 6)
          test("neg") - assert(CommutativeMonoid[Double :| Negative].combine(-1, -5) == -6)
      }
    }

    test("eitherNec"):
      import io.github.iltotore.iron.cats.*

      val eitherNecWithFailingPredicate = Temperature.eitherNec(-5)
      assert(eitherNecWithFailingPredicate == Left(NonEmptyChain.one("Should be strictly positive")), "'eitherNec' returns left if predicate fails")
      val eitherNecWithSucceedingPredicate = Temperature.eitherNec(100)
      assert(eitherNecWithSucceedingPredicate == Right(Temperature(100)), "right should contain result of 'apply'")

    test("eitherNel"):
      import io.github.iltotore.iron.cats.*

      val eitherNelWithFailingPredicate = Temperature.eitherNel(-5)
      assert(eitherNelWithFailingPredicate == Left(NonEmptyList.one("Should be strictly positive")), "'eitherNel' returns left if predicate fails")
      val eitherNelWithSucceedingPredicate = Temperature.eitherNel(100)
      assert(eitherNelWithSucceedingPredicate == Right(Temperature(100)), "right should contain result of 'apply'")

    test("validated"):
      import io.github.iltotore.iron.cats.*

      val validatedWithFailingPredicate = Temperature.validated(-5)
      assert(validatedWithFailingPredicate == Invalid("Should be strictly positive"), "'eitherNec' returns left if predicate fails")
      val validatedWithSucceedingPredicate = Temperature.validated(100)
      assert(validatedWithSucceedingPredicate == Valid(Temperature(100)), "right should contain result of 'apply'")

    test("validatedNec"):
      import io.github.iltotore.iron.cats.*

      val validatedNecWithFailingPredicate = Temperature.validatedNec(-5)
      assert(
        validatedNecWithFailingPredicate == Invalid(NonEmptyChain.one("Should be strictly positive")),
        "'validatedNec' returns left if predicate fails"
      )
      val validatedNecWithSucceedingPredicate = Temperature.validatedNec(100)
      assert(validatedNecWithSucceedingPredicate == Valid(Temperature(100)), "valid should contain result of 'apply'")

    test("validatedNel"):
      import io.github.iltotore.iron.cats.*

      val validatedNelWithFailingPredicate = Temperature.validatedNel(-5)
      assert(
        validatedNelWithFailingPredicate == Invalid(NonEmptyList.one("Should be strictly positive")),
        "'validatedNel' returns left if predicate fails"
      )
      val validatedNelWithSucceedingPredicate = Temperature.validatedNel(100)
      assert(validatedNelWithSucceedingPredicate == Valid(Temperature(100)), "valid should contain result of 'apply'")

    test("all"):
      test("functoToMapLogic"):
        test - assert(Temperature.optionAll(NonEmptyList.of(1, 2, -3)).isEmpty)
        test - assert(Temperature.optionAll(NonEmptyList.of(1, 2, 3)).contains(NonEmptyList.of(Temperature(1), Temperature(2), Temperature(3))))

      val valid = List(1, 2, 3)
      val invalid = List(1, -2, -3)

      test("simple"):
        test("eitherNec"):
          test - assert(valid.refineAllNec[Positive] == Right(valid))
          test - assert(invalid.refineAllNec[Positive] == Left(NonEmptyChain.of(
            InvalidValue(-2, "Should be strictly positive"),
            InvalidValue(-3, "Should be strictly positive")
          )))

          test("eitherNel"):
            test - assert(valid.refineAllNel[Positive] == Right(valid))
            test - assert(invalid.refineAllNel[Positive] == Left(NonEmptyList.of(
              InvalidValue(-2, "Should be strictly positive"),
              InvalidValue(-3, "Should be strictly positive")
            )))

          test("validatedNec"):
            test - assert(valid.refineAllValidatedNec[Positive] == Valid(valid))
            test - assert(invalid.refineAllValidatedNec[Positive] == Invalid(NonEmptyChain.of(
              InvalidValue(-2, "Should be strictly positive"),
              InvalidValue(-3, "Should be strictly positive")
            )))

            test("validatedNel"):
              test - assert(valid.refineAllValidatedNel[Positive] == Valid(valid))
              test - assert(invalid.refineAllValidatedNel[Positive] == Invalid(NonEmptyList.of(
                InvalidValue(-2, "Should be strictly positive"),
                InvalidValue(-3, "Should be strictly positive")
              )))

      test("further"):

        val furtherValid = List(2, 4, 6).refineAllUnsafe[Positive]
        val furtherInvalid = List(1, 2, 3).refineAllUnsafe[Positive]

        test("eitherNec"):
          test - assert(furtherValid.refineAllFurtherNec[Even] == Right(furtherValid))
          test - assert(furtherInvalid.refineAllFurtherNec[Even] == Left(NonEmptyChain.of(
            InvalidValue(1, "Should be a multiple of 2"),
            InvalidValue(3, "Should be a multiple of 2")
          )))

        test("eitherNel"):
          test - assert(furtherValid.refineAllFurtherNel[Even] == Right(furtherValid))
          test - assert(furtherInvalid.refineAllFurtherNel[Even] == Left(NonEmptyList.of(
            InvalidValue(1, "Should be a multiple of 2"),
            InvalidValue(3, "Should be a multiple of 2")
          )))

        test("validatedNec"):
          test - assert(furtherValid.refineAllFurtherValidatedNec[Even] == Valid(furtherValid))
          test - assert(furtherInvalid.refineAllFurtherValidatedNec[Even] == Invalid(NonEmptyChain.of(
            InvalidValue(1, "Should be a multiple of 2"),
            InvalidValue(3, "Should be a multiple of 2")
          )))

        test("validatedNel"):
          test - assert(furtherValid.refineAllFurtherValidatedNel[Even] == Valid(furtherValid))
          test - assert(furtherInvalid.refineAllFurtherValidatedNel[Even] == Invalid(NonEmptyList.of(
            InvalidValue(1, "Should be a multiple of 2"),
            InvalidValue(3, "Should be a multiple of 2")
          )))

      test("ops"):
        test("eitherNec"):
          test - assert(Temperature.eitherAllNec(valid) == Right(Temperature.assumeAll(valid)))
          test - assert(Temperature.eitherAllNec(invalid) == Left(NonEmptyChain.of(
            InvalidValue(-2, "Should be strictly positive"),
            InvalidValue(-3, "Should be strictly positive")
          )))

        test("eitherNel"):
          test - assert(Temperature.eitherAllNel(valid) == Right(Temperature.assumeAll(valid)))
          test - assert(Temperature.eitherAllNel(invalid) == Left(NonEmptyList.of(
            InvalidValue(-2, "Should be strictly positive"),
            InvalidValue(-3, "Should be strictly positive")
          )))

        test("validatedNec"):
          test - assert(Temperature.validatedAllNec(valid) == Valid(Temperature.assumeAll(valid)))
          test - assert(Temperature.validatedAllNec(invalid) == Invalid(NonEmptyChain.of(
            InvalidValue(-2, "Should be strictly positive"),
            InvalidValue(-3, "Should be strictly positive")
          )))

        test("validatedNel"):
          test - assert(Temperature.validatedAllNel(valid) == Valid(Temperature.assumeAll(valid)))
          test - assert(Temperature.validatedAllNel(invalid) == Invalid(NonEmptyList.of(
            InvalidValue(-2, "Should be strictly positive"),
            InvalidValue(-3, "Should be strictly positive")
          )))

  }
