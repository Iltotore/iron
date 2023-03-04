package io.github.iltotore.iron.scalacheck

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.string.*
import io.github.iltotore.iron.scalacheck.string.given
import org.scalacheck.*
import utest.*

object StringSuite extends TestSuite:

  val tests: Tests = Tests {
    test("startWith") - testGen[String, StartWith["abc"]]
    test("endWith") - testGen[String, EndWith["abc"]]
  }
