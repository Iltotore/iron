package io.github.iltotore.scalalint.constraint.numeric

import io.github.iltotore.scalalint.constraint._

trait Natural extends ConstraintAnchor

implicit object Natural extends Constraint[Double, Natural] {
  
  override def assert(value: Double): Option[String] = if(value % 1 != 0 || value < 0) Some(s"$value is not natural") else None
}