package io.github.iltotore.iron.scalacheck

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.constraint.numeric.*
import io.github.iltotore.iron.scalacheck.all.given
import org.scalacheck.*
import utest.*

object CollectionSuite extends TestSuite:

  val tests: Tests = Tests {
    test("minLength") {
      test("seq") - testGen[Seq[Boolean], MinLength[5]]
      test("string") - testGen[String, MinLength[5]]
    }
    test("maxLength") {
      test("seq") - testGen[Seq[Boolean], MaxLength[5]]
      test("string") - testGen[String, MaxLength[5]]
    }
    test("empty") {
      test("seq") - testGen[Seq[Boolean], Empty]
      test("string") - testGen[String, Empty]
      test("not empty") - testGen[Seq[Boolean], Not[Empty]]
    }
    test("contain") {
      test("seq") - testGen[Seq[Boolean], Contain[true]]
      test("string") - testGen[String, Contain["a"]]
    }
    test("forAll") {
      test("seq") - testGen[Seq[Boolean], ForAll[StrictEqual[true]]]
      test("string") - testGen[String, ForAll[StrictEqual['a']]]
    }
    test("init") {
      test("seq") - testGen[Seq[Boolean], Init[StrictEqual[true]]]
      test("string") - testGen[String, Init[StrictEqual['a']]]
    }
    test("tail") {
      test("seq") - testGen[Seq[Boolean], Tail[StrictEqual[true]]]
      test("string") - testGen[String, Tail[StrictEqual['a']]]
    }
    test("head") {
      test("seq") - testGen[Seq[Boolean], Head[StrictEqual[true]]]
      test("string") - testGen[String, Head[StrictEqual['a']]]
    }
    test("last") {
      test("seq") - testGen[Seq[Boolean], Last[StrictEqual[true]]]
      test("string") - testGen[String, Last[StrictEqual['a']]]
    }
  }