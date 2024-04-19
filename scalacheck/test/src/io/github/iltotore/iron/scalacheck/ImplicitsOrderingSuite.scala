package io.github.iltotore.iron.scalacheck

import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.scalacheck.all.given
import utest.*

object ImplicitsOrderingSuite extends TestSuite:

  val tests: Tests = Tests:
    test("should resolve implicits using all.given import") - testGen[String, Empty]
