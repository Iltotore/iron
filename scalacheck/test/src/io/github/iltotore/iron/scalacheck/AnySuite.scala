package io.github.iltotore.iron.scalacheck

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.any.*
import io.github.iltotore.iron.scalacheck.any.given
import org.scalacheck.*
import utest.*

object AnySuite extends TestSuite:

  val tests: Tests = Tests {
    test("fallback") - testGen[Boolean, StrictEqual[true]]

    test("union") - testGen[Int, StrictEqual[1] | StrictEqual[2] | StrictEqual[3]]
  }
