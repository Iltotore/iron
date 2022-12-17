package io.github.iltotore.iron.constraint

import io.github.iltotore.iron.Constraint
import io.github.iltotore.iron.compileTime.*

import scala.compiletime.constValue
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

      override inline def test(value: String): Boolean = ${ check('value, '{ constValue[V] }) }

      override inline def message: String = "Should contain the string " + constValue[V]

    private def check(expr: Expr[String], partExpr: Expr[String])(using Quotes): Expr[Boolean] =
      (expr.value, partExpr.value) match
        case (Some(value), Some(part)) => Expr(value.contains(part))
        case _                         => '{ ${ expr }.contains($partExpr) }
