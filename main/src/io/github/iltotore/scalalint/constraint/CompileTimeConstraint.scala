package io.github.iltotore.scalalint.constraint

import scala.quoted._

trait CompileTimeConstraint[T, C <: ConstraintAnchor] extends Constraint[T, C] {

  //override inline def assert(value: T): Unit = compileTime(assertCompileTime(value))

  def assertCompileTime(value: T): Unit
}