package io.github.iltotore.scalalint.constraint.numeric

import io.github.iltotore.scalalint.constraint._

trait NotNull extends ConstraintAnchor

implicit object NotNull extends Constraint[Double, NotNull] {

  override def assert(value: Double): Option[String] = if(value == 0) Some(s"$value shouldn't be zero") else None
  
  trait CompileTime extends ConstraintAnchor
  
  implicit object CompileTime extends CompileTimeConstraint[Double, NotNull.CompileTime] {

    override inline def assertCompileTime(inline value: Double): Option[(Boolean, String)] = if(value == 0) Some((true, "value shouldn't be zero")) else None
  }
}