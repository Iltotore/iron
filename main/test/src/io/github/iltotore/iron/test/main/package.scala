package io.github.iltotore.iron.test

import io.github.iltotore.iron.constraint.Constraint

package object main {

  trait Positive

  inline given Constraint[Int, Positive] with {
    override inline def assert(value: Int): Boolean = value > 0

    override inline def getMessage(value: Int): String = s"$value should be positive"
  }

  trait Even

  inline given Constraint[Int, Even] with {
    override inline def assert(value: Int): Boolean = value % 2 == 0

    override inline def getMessage(value: Int): String = s"$value should be even"
  }
}