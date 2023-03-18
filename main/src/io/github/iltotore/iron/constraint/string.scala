package io.github.iltotore.iron.constraint

import io.github.iltotore.iron.{==>, Constraint, Implication}
import io.github.iltotore.iron.constraint.any.*
import io.github.iltotore.iron.constraint.collection.*
import io.github.iltotore.iron.compileTime.*
import io.github.iltotore.iron.constraint.char.{Digit, Letter, LowerCase, UpperCase, Whitespace}

import scala.compiletime.constValue
import scala.quoted.*

/**
 * [[String]]-related constraints.
 *
 * @see [[collection]]
 */
object string:

  /**
   * Tests if the input only contains whitespaces.
   * @see [[Whitespace]]
   */
  type Blank = ForAll[Whitespace] DescribedAs "Should only contain whitespaces"

  /**
   * Tests if the input does not have leading or trailing whitespaces.
   * @see [[Whitespace]]
   */
  type Trimmed = (Empty | Not[Head[Whitespace] | Last[Whitespace]]) DescribedAs
    "Should not have leading or trailing whitespaces"

  /**
   * Tests if all letters of the input are lower cased.
   */
  type LettersLowerCase = ForAll[Not[Letter] | LowerCase] DescribedAs "All letters should be lower cased"

  /**
   * Tests if all letters of the input are upper cased.
   */
  type LettersUpperCase = ForAll[Not[Letter] | UpperCase] DescribedAs "All letters should be upper cased"

  /**
   * Tests if the input only contains alphanumeric characters.
   */
  type Alphanumeric = ForAll[Digit | Letter] DescribedAs "Should be alphanumeric"

  /**
   * Tests if the input starts with the given prefix.
   * @tparam V the string to compare with the start of the input.
   */
  final class StartWith[V <: String]

  /**
   * Tests if the input ends with the given suffix.
   *
   * @tparam V the string to compare with the end of the input.
   */
  final class EndWith[V <: String]

  /**
   * Tests if the input matches the given regex.
   *
   * @tparam V the pattern to match against the input.
   */
  final class Match[V <: String]

  /**
   * Tests if the input is a valid URL.
   *
   * @note it only checks if the input fits the URL pattern. Not if the given URL exists/is accessible.
   */
  type ValidURL =
    Match[
      "((\\w+:)+\\/\\/)?(([-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6})|(localhost))(:\\d{1,5})?(\\/|\\/([-a-zA-Z0-9@:%_\\+.~#?&//=]*))?"
    ] DescribedAs "Should be an URL"

  /**
   * Tests if the input is a valid UUID.
   */
  type ValidUUID =
    Match["^([0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12})"] DescribedAs "Should be an UUID"

  object Blank:

    given (Empty ==> Blank) = Implication()

  object StartWith:

    inline given [V <: String]: Constraint[String, StartWith[V]] with

      override inline def test(value: String): Boolean = ${ check('value, '{ constValue[V] }) }

      override inline def message: String = "Should start with " + stringValue[V]

    private def check(expr: Expr[String], prefixExpr: Expr[String])(using Quotes): Expr[Boolean] =
      (expr.value, prefixExpr.value) match
        case (Some(value), Some(prefix)) => Expr(value.startsWith(prefix))
        case _                           => '{ $expr.startsWith($prefixExpr) }

  object EndWith:

    inline given [V <: String]: Constraint[String, EndWith[V]] with

      override inline def test(value: String): Boolean = ${ check('value, '{ constValue[V] }) }

      override inline def message: String = "Should end with " + stringValue[V]

    private def check(expr: Expr[String], prefixExpr: Expr[String])(using Quotes): Expr[Boolean] =
      (expr.value, prefixExpr.value) match
        case (Some(value), Some(prefix)) => Expr(value.endsWith(prefix))
        case _                           => '{ $expr.endsWith($prefixExpr) }

  object Match:

    inline given [V <: String]: Constraint[String, Match[V]] with

      override inline def test(value: String): Boolean = ${ check('value, '{ constValue[V] }) }

      override inline def message: String = "Should match " + constValue[V]

    private def check(valueExpr: Expr[String], regexExpr: Expr[String])(using Quotes): Expr[Boolean] =
      (valueExpr.value, regexExpr.value) match
        case (Some(value), Some(regex)) => Expr(value.matches(regex))
        case _                          => '{ $valueExpr.matches($regexExpr) }
