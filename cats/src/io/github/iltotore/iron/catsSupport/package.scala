package io.github.iltotore.iron

import cats.data.NonEmptyList
import cats.Semigroup
import io.github.iltotore.iron.constraint.IllegalValueError

package object catsSupport {

  type AccumulatedRefined[A] = Either[NonEmptyList[IllegalValueError[?]], A]

  extension [A](refined: Refined[A]) {

    def accumulated: AccumulatedRefined[A] = refined.left.map(NonEmptyList.one)
  }
}