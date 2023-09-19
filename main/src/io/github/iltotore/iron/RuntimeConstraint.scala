package io.github.iltotore.iron

final class RuntimeConstraint[A, B](_test: A => Boolean, val message: String):
  inline def test(value: A): Boolean = _test(value)

object RuntimeConstraint:
  inline given derived[A, B](using c: Constraint[A, B]): RuntimeConstraint[A, B] =
    new RuntimeConstraint[A, B](c.test(_), c.message)
