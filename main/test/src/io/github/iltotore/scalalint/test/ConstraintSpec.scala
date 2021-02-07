package io.github.iltotore.scalalint.test

import org.scalatest._
import flatspec._
import matchers._

class ConstraintSpec extends AnyFlatSpec with should.Matchers {

  "The Natural constraint" should "throw an error if the number isn't natural" in {
    val inputs = Map(
      -1 -> false,
      1 -> true,
      -1.5 -> false,
      1.5 -> false
    )
    
    for(input <- inputs) assert(Natural.assert(input(0)) == input(1))
  }
}
