package io.github.iltotore

import scala.language.implicitConversions
import io.github.iltotore.iron.constraint.{Behavior, Constrained}


package object iron {

  implicit inline def valueToConstrained[A, B](value: A)(using behavior: Behavior[A, B]): Constrained[A, B] = {
    compileTime.preAssert(behavior.assert(value))
    Constrained(value)
  }
}