package io.github.iltotore.scalalint.constraint.numeric

import io.github.iltotore.scalalint.constraint._

trait Positive extends ConstraintAnchor

implicit object Positive extends Constraint[Double, Positive] {

  override def assert(value: Double): Option[String] = if(value < 0) Some("$value is not positive") else None
}