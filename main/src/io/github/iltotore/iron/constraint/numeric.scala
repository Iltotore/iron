package io.github.iltotore.iron.constraint

import io.github.iltotore.iron._, constraint._

object numeric {

  type Positive = Int
  given Behavior[Int, Positive] with {
    override inline def assert(value: Int): Boolean = value > 0
  }

}