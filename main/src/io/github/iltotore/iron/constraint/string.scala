package io.github.iltotore.iron.constraint

import io.github.iltotore.iron.{==>, Constraint, Implication}
import io.github.iltotore.iron.constraint.any.*
import io.github.iltotore.iron.constraint.collection.*
import io.github.iltotore.iron.compileTime.*
import io.github.iltotore.iron.constraint.char.{Digit, Letter, LowerCase, UpperCase, Whitespace}
import io.github.iltotore.iron.macros.reflectUtil

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
  type Blank = DescribedAs[ForAll[Whitespace], "Should only contain whitespaces"]

  /**
   * Tests if the input does not have leading or trailing whitespaces.
   * @see [[Whitespace]]
   */
  type Trimmed = DescribedAs[
    Empty | Not[Head[Whitespace] | Last[Whitespace]],
    "Should not have leading or trailing whitespaces"
  ]

  /**
   * Tests if all letters of the input are lower cased.
   */
  type LettersLowerCase = DescribedAs[ForAll[Not[Letter] | LowerCase], "All letters should be lower cased"]

  /**
   * Tests if all letters of the input are upper cased.
   */
  type LettersUpperCase = DescribedAs[ForAll[Not[Letter] | UpperCase], "All letters should be upper cased"]

  /**
   * Tests if the input only contains alphanumeric characters.
   */
  type Alphanumeric = DescribedAs[ForAll[Digit | Letter], "Should be alphanumeric"]

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
  type ValidURL = DescribedAs[
    Match["((\\w+:)+\\/\\/)?(([-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,63})|(localhost))(:\\d{1,5})?(\\/|\\/([-a-zA-Z0-9@:%_\\+.~#?&//=]*))?"],
    "Should be an URL"
  ]

  /**
   * Tests if the input is a valid UUID.
   */
  type ValidUUID = DescribedAs[
    Match["^([0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12})"],
    "Should be an UUID"
  ]

  /**
   * Tests if the input is a valid semantic version as defined in [semver site](https://semver.org/#is-there-a-suggested-regular-expression-regex-to-check-a-semver-string).
   */
  type SemanticVersion =
    Match[
      "^v?(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$"
    ]
  object Blank:

    given (Empty ==> Blank) = Implication()

  object StartWith:

    inline given [V <: String]: Constraint[String, StartWith[V]] with

      override inline def test(inline value: String): Boolean = ${ check('value, '{ constValue[V] }) }

      override inline def message: String = "Should start with " + stringValue[V]

    private def check(expr: Expr[String], prefixExpr: Expr[String])(using Quotes): Expr[Boolean] =
      val rflUtil = reflectUtil
      import rflUtil.*

      (expr.decode, prefixExpr.decode) match
        case (Right(value), Right(prefix)) => Expr(value.startsWith(prefix))
        case _                             => '{ $expr.startsWith($prefixExpr) }

  object EndWith:

    inline given [V <: String]: Constraint[String, EndWith[V]] with

      override inline def test(inline value: String): Boolean = ${ check('value, '{ constValue[V] }) }

      override inline def message: String = "Should end with " + stringValue[V]

    private def check(expr: Expr[String], prefixExpr: Expr[String])(using Quotes): Expr[Boolean] =
      val rflUtil = reflectUtil
      import rflUtil.*

      (expr.decode, prefixExpr.decode) match
        case (Right(value), Right(prefix)) => Expr(value.endsWith(prefix))
        case _                             => '{ $expr.endsWith($prefixExpr) }

  object Match:

    inline given [V <: String]: Constraint[String, Match[V]] with

      override inline def test(inline value: String): Boolean = ${ check('value, '{ constValue[V] }) }

      override inline def message: String = "Should match " + constValue[V]

    private def check(valueExpr: Expr[String], regexExpr: Expr[String])(using Quotes): Expr[Boolean] =
      val rflUtil = reflectUtil
      import rflUtil.*

      (valueExpr.decode, regexExpr.decode) match
        case (Right(value), Right(regex)) => Expr(value.matches(regex))
        case _                            => '{ $valueExpr.matches($regexExpr) }
