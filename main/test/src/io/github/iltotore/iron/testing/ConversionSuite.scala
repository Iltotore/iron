package io.github.iltotore.iron
package testing

import io.github.iltotore.iron.constraint.numeric.*
import utest.*

object ConversionSuite extends TestSuite:

  val tests: Tests = Tests:

    test("autoCastIron"):
      test("widen"):
        val narrow: Int :| Greater[5] = 10
        val wide: Int :| Greater[0] = narrow
        assert(wide > 0)

    test("autoCastIronOption"):
      test("widen"):
        // `narrow` has the element type committed, as produced by e.g. cats' `.some`,
        // so the expected element type can no longer reach the element itself.
        val narrow: Option[Int :| Greater[5]] = Some(10)
        val wide: Option[Int :| Greater[0]] = narrow
        assert(wide.exists(_ > 0))

      test("none"):
        val narrow: Option[Int :| Greater[5]] = None
        val wide: Option[Int :| Greater[0]] = narrow
        assert(wide.isEmpty)
