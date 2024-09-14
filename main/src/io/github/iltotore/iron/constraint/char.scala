package io.github.iltotore.iron.constraint

import io.github.iltotore.iron.Constraint
import io.github.iltotore.iron.compileTime.*
import io.github.iltotore.iron.constraint.any.Not
import io.github.iltotore.iron.macros.reflectUtil

import scala.quoted.*

object char:

  /**
   * Tests if the input is whitespace.
   */
  final class Whitespace

  /**
   * Tests if the given input is lower-cased.
   */
  final class LowerCase

  /**
   * Tests if the input is upper-cased.
   */
  final class UpperCase

  /**
   * Tests if the input is a digit (from 0 to 9).
   */
  final class Digit

  /**
   * Tests if the input is a letter (from a to z, case insensitive).
   */
  final class Letter

  /**
   * Tests if the input is neither a digit or a letter.
   */
  type Special = Not[Digit] & Not[Letter]

  object Whitespace:

    inline given Constraint[Char, Whitespace] with

      override inline def test(inline value: Char): Boolean = ${ check('value) }

      override inline def message: String = "Should be a whitespace"

    private def check(expr: Expr[Char])(using Quotes): Expr[Boolean] =
      val rflUtil = reflectUtil
      import rflUtil.*

      expr.decode match
        case Right(value) => Expr(value.isWhitespace)
        case _            => '{ $expr.isWhitespace }

  object LowerCase:

    inline given Constraint[Char, LowerCase] with

      override inline def test(inline value: Char): Boolean = ${ check('value) }

      override inline def message: String = "Should be a lower cased"

    private def check(expr: Expr[Char])(using Quotes): Expr[Boolean] =
      val rflUtil = reflectUtil
      import rflUtil.*

      expr.decode match
        case Right(value) => Expr(value.isLower)
        case _            => '{ $expr.isLower }

  object UpperCase:

    inline given Constraint[Char, UpperCase] with

      override inline def test(inline value: Char): Boolean = ${ check('value) }

      override inline def message: String = "Should be a upper cased"

    private def check(expr: Expr[Char])(using Quotes): Expr[Boolean] =
      val rflUtil = reflectUtil
      import rflUtil.*

      expr.decode match
        case Right(value) => Expr(value.isUpper)
        case _            => '{ $expr.isUpper }

  object Digit:

    inline given Constraint[Char, Digit] with

      override inline def test(inline value: Char): Boolean = ${ check('value) }

      override inline def message: String = "Should be a digit"

    private def check(expr: Expr[Char])(using Quotes): Expr[Boolean] =
      val rflUtil = reflectUtil
      import rflUtil.*

      expr.decode match
        case Right(value) => Expr(value.isDigit)
        case _            => '{ $expr.isDigit }

  object Letter:

    inline given Constraint[Char, Letter] with

      override inline def test(inline value: Char): Boolean = ${ check('value) }

      override inline def message: String = "Should be a letter"

    private def check(expr: Expr[Char])(using Quotes): Expr[Boolean] =
      val rflUtil = reflectUtil
      import rflUtil.*

      expr.decode match
        case Right(value) => Expr(value.isLetter)
        case _            => '{ $expr.isLetter }
