package io.github.iltotore.iron.constraint

import io.github.iltotore.iron.Constraint
import io.github.iltotore.iron.constraint.any.*
import io.github.iltotore.iron.constraint.collection.*
import io.github.iltotore.iron.compileTime.*
import scala.compiletime.constValue
import scala.quoted.*

/**
 * [[String]]-related constraints.
 *
 * @see [[collection]]
 */
object string:
  /**
   * Tests if the given input is lower-cased.
   */
  final class LowerCase

  /**
   * Tests if the input is upper-cased.
   */
  final class UpperCase

  /**
   * Tests if the input matches the given regex.
   *
   * @tparam V the pattern to match against the input.
   */
  final class Match[V <: String]

  /**
   * Tests if the input only contains alphanumeric characters.
   */
  type Alphanumeric = Match["^[a-zA-Z0-9]+"] DescribedAs "Should be alphanumeric"

  /**
   * Tests if the input is a valid URL.
   *
   * @note it only checks if the input fits the URL pattern. Not if the given URL exists/is accessible.
   */
  type URLLike =
    Match["^(?:http(s)?:\\/\\/)?[\\w.-]+(?:\\.[\\w\\.-]+)+[\\w\\-\\._~:/?#\\[\\]@!\\$&'\\(\\)\\*\\+,;=.]+$"] DescribedAs "Should be an URL"

  /**
   * Tests if the input is a valid UUID.
   */
  type UUIDLike =
    Match["^([0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12})"] DescribedAs "Should be an UUID"

  object LowerCase:
    inline given Constraint[String, LowerCase] with

      override inline def test(value: String): Boolean = ${ checkLowerCase('value) }

      override inline def message: String = "Should be lower cased"

    def checkLowerCase(valueExpr: Expr[String])(using Quotes): Expr[Boolean] =
      valueExpr.value match
        case Some(value) => Expr(value equals value.toLowerCase)
        case None        => '{ $valueExpr equals $valueExpr.toLowerCase }

  object UpperCase:
    inline given Constraint[String, UpperCase] with

      override inline def test(value: String): Boolean = ${ checkUpperCase('value) }

      override inline def message: String = "Should be upper cased"

    private def checkUpperCase(valueExpr: Expr[String])(using Quotes): Expr[Boolean] =
      valueExpr.value match
        case Some(value) => Expr(value equals value.toUpperCase)
        case None        => '{ $valueExpr equals $valueExpr.toUpperCase }

  object Match:
    inline given [V <: String]: Constraint[String, Match[V]] with

      override inline def test(value: String): Boolean = ${ checkMatch('value, '{ constValue[V] }) }

      override inline def message: String = "Should match " + constValue[V]

    def checkMatch(valueExpr: Expr[String], regexExpr: Expr[String])(using Quotes): Expr[Boolean] =
      (valueExpr.value, regexExpr.value) match
        case (Some(value), Some(regex)) => Expr(value.matches(regex))
        case _                          => '{ $valueExpr.matches($regexExpr) }
