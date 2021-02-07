package io.github.iltotore.scalalint.constraint.numeric

import io.github.iltotore.scalalint.constraint._

trait Natural extends ConstraintAnchor

implicit object Natural extends Constraint[Double, Natural] {
  
  override def assert(value: Double): Unit = if(value % 1 != 0 || value < 0) throw AssertionError(s"$value is not natural")
}