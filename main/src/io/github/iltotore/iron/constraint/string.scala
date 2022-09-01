package io.github.iltotore.iron.constraint

import io.github.iltotore.iron.Constraint
import io.github.iltotore.iron.constraint.any.{*, given}
import io.github.iltotore.iron.ops.*
import scala.compiletime.constValue

/**
 * [[String]]-related constraints.
 *
 * @see [[collection]]
 */
object string:

  export collection.{MinLength, MaxLength, Contain}

  inline given [V <: Int]: Constraint[String, MinLength[V]] with

    override inline def test(value: String): Boolean = macros.checkMinLength(value, constValue[V])

    override inline def message: String = "Should have a min length of " + stringValue[V]

  inline given [V <: Int]: Constraint[String, MaxLength[V]] with

    override inline def test(value: String): Boolean = macros.checkMaxLength(value, constValue[V])

    override inline def message: String = "Should have a max length of " + stringValue[V]

  inline given [V <: String]: Constraint[String, Contain[V]] with

    override inline def test(value: String): Boolean = macros.checkContain(value, constValue[V])

    override inline def message: String = "Should contain the string " + constValue[V]

  /**
   * Tests if the given input is lower-cased.
   */
  final class LowerCase

  inline given Constraint[String, LowerCase] with

    override inline def test(value: String): Boolean = macros.checkLowerCase(value)

    override inline def message: String = "Should be lower cased"

  /**
   * Tests if the input is upper-cased.
   */
  final class UpperCase

  inline given Constraint[String, UpperCase] with

    override inline def test(value: String): Boolean = macros.checkUpperCase(value)

    override inline def message: String = "Should be upper cased"

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

  inline given [V <: String]: Constraint[String, Match[V]] with

    override inline def test(value: String): Boolean = macros.checkMatch(value, constValue[V])

    override inline def message: String = "Should match " + constValue[V]
