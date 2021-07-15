package io.github.iltotore.iron

import cats.Semigroup
import cats.data.{ValidatedNec, ValidatedNel}
import cats.syntax.either.*
import io.github.iltotore.iron.constraint.IllegalValueError

/**
 * The package is named `catsSupport` to avoid clash with cats' root package.
 */
package object catsSupport {

  type RefinedNec[A] = ValidatedNec[IllegalValueError[?], A]
  type RefinedNel[A] = ValidatedNel[IllegalValueError[?], A]

  extension [A](refined: Refined[A]) {

    /**
     * Transforms this Refined[A] to a ValidatedNec.
     * @return this value as RefinedNec[A]
     */
    def validatedNec: RefinedNec[A] = refined.toValidatedNec

    /**
     * Transforms this Refined[A] to a ValidatedNel.
     * @return this value as RefinedNel[A]
     */
    def validatedNel: RefinedNel[A] = refined.toValidatedNel

  }
}