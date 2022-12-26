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
    Match[
      "((\\w+:)+\\/\\/)?(([-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6})|(localhost))(:\\d{1,5})?(\\/|\\/([-a-zA-Z0-9@:%_\\+.~#?&//=]*))?"
    ] DescribedAs "Should be an URL"

  /**
   * Tests if the input is a valid UUID.
   */
  type UUIDLike =
    Match["^([0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12})"] DescribedAs "Should be an UUID"

  object Match:
    inline given [V <: String]: Constraint[String, Match[V]] with

      override inline def test(value: String): Boolean = ${ check('value, '{ constValue[V] }) }

      override inline def message: String = "Should match " + constValue[V]

    private def check(valueExpr: Expr[String], regexExpr: Expr[String])(using Quotes): Expr[Boolean] =
      (valueExpr.value, regexExpr.value) match
        case (Some(value), Some(regex)) => Expr(value.matches(regex))
        case _                          => '{ $valueExpr.matches($regexExpr) }
