package io.github.iltotore.iron.constraint

import io.github.iltotore.iron.{:|, ==>, Constraint, Implication}
import io.github.iltotore.iron.compileTime.{*, given}

import scala.compiletime.{constValue, summonInline}
import scala.compiletime.ops.string.Length
import scala.quoted.*

/**
 * Collection-related constraints.
 *
 * @see [[string]] which contains [[String]]-specific implementations for most of these constraints.
 */
object collection:

  /**
   * Tests minimum length. Supports [[Iterable]] and [[String]] by default.
   *
   * @tparam V the minimum length of the tested input
   */
  final class MinLength[V <: Int]

  /**
   * Tests maximum length. Supports [[Iterable]] and [[String]] by default.
   *
   * @tparam V the maximum length of the tested input
   */
  final class MaxLength[V <: Int]

  /**
   * Tests if the given collection contains a specific value.
   *
   * @tparam V the value the input must contain.
   */
  final class Contain[V]

  /**
   * Tests if each element satisfies the given constraint.
   * @tparam C the constraint to test against each element.
   */
  final class ForAll[C]

  /**
   * Tests if at least one element satisfies the given constraint.
   *
   * @tparam C the constraint to test against each element.
   */
  final class Exists[C]

  object MinLength:
    inline given [V <: Int, I <: Iterable[?]]: Constraint[I, MinLength[V]] with

      override inline def test(value: I): Boolean = value.sizeCompare(constValue[V]) >= 0

      override inline def message: String = "Should contain atleast " + stringValue[V] + " elements"

    inline given [V <: Int]: Constraint[String, MinLength[V]] with

      override inline def test(value: String): Boolean = ${ check('value, '{ constValue[V] }) }

      override inline def message: String = "Should have a min length of " + stringValue[V]

    private def check(expr: Expr[String], lengthExpr: Expr[Int])(using Quotes): Expr[Boolean] =
      (expr.value, lengthExpr.value) match
        case (Some(value), Some(minLength)) => Expr(value.length >= minLength)
        case _                              => '{ ${ expr }.length >= $lengthExpr }

  object MaxLength:
    inline given [V <: Int, I <: Iterable[?]]: Constraint[I, MaxLength[V]] with

      override inline def test(value: I): Boolean = value.sizeCompare(constValue[V]) <= 0

      override inline def message: String = "Should contain at most " + stringValue[V] + " elements"

    inline given [V <: Int]: Constraint[String, MaxLength[V]] with

      override inline def test(value: String): Boolean = ${ checkMaxLength('value, '{ constValue[V] }) }

      override inline def message: String = "Should have a max length of " + stringValue[V]

    private def checkMaxLength(expr: Expr[String], lengthExpr: Expr[Int])(using Quotes): Expr[Boolean] =
      (expr.value, lengthExpr.value) match
        case (Some(value), Some(maxLength)) => Expr(value.length <= maxLength)
        case _                              => '{ ${ expr }.length <= $lengthExpr }

  object Contain:
    inline given [A, V <: A, I <: Iterable[A]]: Constraint[I, Contain[V]] with

      override inline def test(value: I): Boolean = value.iterator.contains(constValue[V])

      override inline def message: String = "Should contain at most " + stringValue[V] + " elements"

    inline given [V <: String]: Constraint[String, Contain[V]] with

      override inline def test(value: String): Boolean = ${ checkString('value, '{ constValue[V] }) }

      override inline def message: String = "Should contain the string " + constValue[V]

    private def checkString(expr: Expr[String], partExpr: Expr[String])(using Quotes): Expr[Boolean] =
      (expr.value, partExpr.value) match
        case (Some(value), Some(part)) => Expr(value.contains(part))
        case _                         => '{ ${ expr }.contains($partExpr) }

  object ForAll:

    class ForAllIterable[A, I <: Iterable[A], C, Impl <: Constraint[A, C]](using Impl) extends Constraint[I, ForAll[C]]:

      override inline def test(value: I): Boolean = value.forall(summonInline[Impl].test(_))

      override inline def message: String = "For each element: (" + summonInline[Impl].message + ")"

    inline given [A, I <: Iterable[A], C, Impl <: Constraint[A, C]](using inline impl: Impl): ForAllIterable[A, I, C, Impl] =
      new ForAllIterable

    class ForAllString[C, Impl <: Constraint[Char, C]](using Impl) extends Constraint[String, ForAll[C]]:

      override inline def test(value: String): Boolean = ${ checkString('value, '{ summonInline[Impl] }) }

      override inline def message: String = "For each element: (" + summonInline[Impl].message + ")"

    inline given forAllString[C, Impl <: Constraint[Char, C]](using inline impl: Impl): ForAllString[C, Impl] = new ForAllString

    private def checkString[C, Impl <: Constraint[Char, C]](expr: Expr[String], constraintExpr: Expr[Impl])(using Quotes): Expr[Boolean] =

      import quotes.reflect.*

      def testChar(char: Expr[Char]): Expr[Boolean] = // Using quotes directly causes a "deferred inline error"
        Apply(Select.unique(constraintExpr.asTerm, "test"), List(char.asTerm)).asExprOf[Boolean]

      expr.value match
        case Some(value) =>
          value
            .map(Expr.apply)
            .map(testChar)
            .foldLeft(Expr(true))((e, t) => '{ $e && $t })

        case None => '{ $expr.forall(c => ${ testChar('c) }) }

  object Exists:

    class ExistsIterable[A, I <: Iterable[A], C, Impl <: Constraint[A, C]](using Impl) extends Constraint[I, Exists[C]]:

      override inline def test(value: I): Boolean = value.exists(summonInline[Impl].test(_))

      override inline def message: String = "At least one: (" + summonInline[Impl].message + ")"

    inline given[A, I <: Iterable[A], C, Impl <: Constraint[A, C]](using inline impl: Impl): ExistsIterable[A, I, C, Impl] =
      new ExistsIterable

    class ExistsString[C, Impl <: Constraint[Char, C]](using Impl) extends Constraint[String, Exists[C]]:

      override inline def test(value: String): Boolean = ${ checkString('value, '{ summonInline[Impl] }) }

      override inline def message: String = "At least one element: (" + summonInline[Impl].message + ")"

    inline given existsString[C, Impl <: Constraint[Char, C]](using inline impl: Impl): ExistsString[C, Impl] = new ExistsString

    private def checkString[C, Impl <: Constraint[Char, C]](expr: Expr[String], constraintExpr: Expr[Impl])(using Quotes): Expr[Boolean] =

      import quotes.reflect.*

      def testChar(char: Expr[Char]): Expr[Boolean] = // Using quotes directly causes a "deferred inline error"
        Apply(Select.unique(constraintExpr.asTerm, "test"), List(char.asTerm)).asExprOf[Boolean]

      expr.value match
        case Some(value) =>
          value
            .map(Expr.apply)
            .map(testChar)
            .foldLeft(Expr(false))((e, t) => '{ $e || $t })

        case None => '{ $expr.exists(c => ${ testChar('c) }) }

    given [C1, C2](using C1 ==> C2): (ForAll[C1] ==> Exists[C2]) = Implication()