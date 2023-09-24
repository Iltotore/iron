package io.github.iltotore.iron

import scala.util.NotGiven

final class RuntimeConstraint[A, C](_test: A => Boolean, val message: String):
  inline def test(value: A): Boolean = _test(value)

object RuntimeConstraint:
  inline given derived[A, C](using inline c: Constraint[A, C]): RuntimeConstraint[A, C] =
    new RuntimeConstraint[A, C](c.test(_), c.message)
