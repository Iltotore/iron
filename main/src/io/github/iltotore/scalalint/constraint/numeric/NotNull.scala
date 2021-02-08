package io.github.iltotore.scalalint.constraint.numeric

import io.github.iltotore.scalalint.constraint._

trait NotNull extends ConstraintAnchor

implicit object NotNull extends Constraint[Double, NotNull] {

  override def assert(value: Double): Option[String] = if(value == 0) Some(s"$value is not positive") else None
}