package io.github.iltotore.iron.scalacheck

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.constraint.numeric.*
import io.github.iltotore.iron.scalacheck.any.given
import io.github.iltotore.iron.scalacheck.collection.given
import io.github.iltotore.iron.scalacheck.numeric.given
import org.scalacheck.*
import utest.*

object CollectionSuite extends TestSuite:

  val tests: Tests = Tests {
    test("minLength") - testGen[Seq[Boolean], MinLength[5]]
    test("maxLength") - testGen[Seq[Boolean], MaxLength[5]]
    test("empty") - testGen[Seq[Boolean], Empty]
    test("contain") - testGen[Seq[Boolean], Contain[5]]
    test("forAll") - testGen[Seq[Boolean], ForAll[StrictEqual[true]]]
    test("init") - testGen[Seq[Boolean], Init[StrictEqual[true]]]
    test("tail") - testGen[Seq[Boolean], Tail[StrictEqual[true]]]
    test("exists") - testGen[Seq[Boolean], Exists[StrictEqual[true]]]
    test("head") - testGen[Seq[Boolean], Head[StrictEqual[true]]]
    test("last") - testGen[Seq[Boolean], Last[StrictEqual[true]]]
  }