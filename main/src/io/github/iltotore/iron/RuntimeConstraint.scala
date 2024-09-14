package io.github.iltotore.iron

import scala.util.NotGiven

/**
 * A [[RuntimeConstraint]] is similar to a [[Constraint]] with the difference that it can be used
 * in non-inlined methods.
 *
 * This allows refinement of values in polymorphic methods / givens without the use of `inline`.
 * e.g., the code below would fail to compile if [[Constraint]] was used instead.
 *
 * {{{
 * def foo[A, C](value: A)(using c: RuntimeConstraint[A, C]): Either[String, A :| C] =
 *   if c.test(value) then Right(value.assume[C]) else Left(c.message)
 * }}}
 *
 * In cases that one does not exist in scope, one will be automatically derived from a [[Constraint]].
 */
final class RuntimeConstraint[A, C](_test: A => Boolean, val message: String):
  inline def test(inline value: A): Boolean = _test(value)

object RuntimeConstraint:
  inline given derived[A, C](using inline c: Constraint[A, C]): RuntimeConstraint[A, C] =
    new RuntimeConstraint[A, C](c.test(_), c.message)
