package io.github.iltotore.iron

import io.github.iltotore.iron.constraint.Constraint

package object test {

  trait Dummy

  inline given Constraint[Boolean, Dummy] with {
    override inline def assert(value: Boolean): Boolean = value

    override inline def getMessage(value: Boolean): String = s"$value is false"
  }

  trait DummyCompileTime

  inline given Constraint.CompileTimeOnly[Boolean, DummyCompileTime] with {
    override inline def assert(value: Boolean): Boolean = value

    override inline def getMessage(value: Boolean): String = s"$value is false"
  }

  trait DummyRuntime

  inline given Constraint.RuntimeOnly[Boolean, DummyRuntime] with {
    override inline def assert(value: Boolean): Boolean = value

    override inline def getMessage(value: Boolean): String = s"$value is false"
  }
}
