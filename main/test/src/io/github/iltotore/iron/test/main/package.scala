package io.github.iltotore.iron.test

import io.github.iltotore.iron.constraint.Constraint

package object main {

  trait DummyCompileTime

  inline given Constraint.CompileTimeOnly[Boolean, DummyCompileTime] with {
    override inline def assert(value: Boolean): Boolean = value
  }

  trait DummyRuntime

  inline given Constraint.RuntimeOnly[Boolean, DummyRuntime] with {
    override inline def assert(value: Boolean): Boolean = value
  }

  trait Positive

  inline given Constraint[Int, Positive] with {
    override inline def assert(value: Int): Boolean = value > 0
  }

  trait Even

  inline given Constraint[Int, Even] with {
    override inline def assert(value: Int): Boolean = value % 2 == 0
  }
}