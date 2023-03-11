package io.github.iltotore.iron.scalacheck

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.char.*
import io.github.iltotore.iron.scalacheck.char.given
import org.scalacheck.*
import utest.*

object CharSuite extends TestSuite:

  val tests: Tests = Tests {

    test("whitespace") - testGen[Char, Whitespace]

    test("lowercase") - testGen[Char, LowerCase]

    test("uppercase") - testGen[Char, UpperCase]

    test("digit") - testGen[Char, Digit]
    
    test("letter") - testGen[Char, Letter]
    
    test("special") - testGen[Char, Special]
  }
