package io.github.iltotore.scalalint.constraint

import io.github.iltotore.scalalint.compileTime

import scala.quoted._

trait CompileTimeConstraint[T, C <: ConstraintAnchor] extends InlinedConstraint[T, C] {

  override inline def assertInlined(inline value: T): Option[String] = compileTime.preAssert(assertCompileTime(value))

  inline def assertCompileTime(inline value: T): Option[String]
}