package io.github.iltotore.scalalint.test

import io.github.iltotore.scalalint.constraint.numeric._
import org.scalatest._
import flatspec._
import matchers._

class ConstraintSpec extends AnyFlatSpec with should.Matchers {

  "The Natural constraint" should "return an error message if the number isn't natural" in {
    val inputs: Map[Double, Boolean] = Map(
      -1.0 -> true,
      1.0 -> false,
      -1.5 -> true,
      1.5 -> true
    )
    
    for(input <- inputs) Natural.assert(input(0)).isDefined shouldBe input(1)
  }
}
