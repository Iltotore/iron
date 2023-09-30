package io.github.iltotore.iron

import _root_.ciris.ConfigDecoder
import utest.*
import ciris.given


object CirisSuite extends TestSuite:
  val tests: Tests = Tests {

    test("summon String => Int :| Pure") {
      summon[ ConfigDecoder[String, Int :| Pure]]
    }

    test("summon from Int => Int :| Pure") {
      summon[ConfigDecoder[Int, Int :| Pure]]
    }
  }

