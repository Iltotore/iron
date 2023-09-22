package io.github.iltotore.iron

type RuntimeConstraint[T] = T match
  case IronType[a, c] => RuntimeConstraint.Impl[a, c, T]

object RuntimeConstraint:
  final class Impl[A, C, T](_test: A => Boolean, val message: String):
    inline def test(value: A): Boolean = _test(value)

  inline given derived[A, C](using inline c: Constraint[A, C]): RuntimeConstraint[A :| C] =
    new Impl[A, C, A :| C](c.test(_), c.message)
