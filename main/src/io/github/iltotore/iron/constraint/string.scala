package io.github.iltotore.iron.constraint

import io.github.iltotore.iron.Constraint
import io.github.iltotore.iron.constraint.any.{*, given}
import io.github.iltotore.iron.ops.*
import scala.compiletime.constValue

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

  final class LowerCase

  inline given Constraint[String, LowerCase] with

    override inline def test(value: String): Boolean = macros.checkLowerCase(value)

    override inline def message: String = "Should be lower cased"

  final class UpperCase

  inline given Constraint[String, UpperCase] with

    override inline def test(value: String): Boolean = macros.checkUpperCase(value)

    override inline def message: String = "Should be upper cased"

  final class Match[V <: String]

  type Alphanumeric = Match["^[a-zA-Z0-9]+"] DescribedAs "Should be alphanumeric"

  type URLLike =
    Match["^(?:http(s)?:\\/\\/)?[\\w.-]+(?:\\.[\\w\\.-]+)+[\\w\\-\\._~:/?#\\[\\]@!\\$&'\\(\\)\\*\\+,;=.]+$"] DescribedAs "Should be an URL"

  type UUIDLike =
    Match["^([0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12})"] DescribedAs "Should be an UUID"

  inline given [V <: String]: Constraint[String, Match[V]] with

    override inline def test(value: String): Boolean = macros.checkMatch(value, constValue[V])

    override inline def message: String = "Should match " + constValue[V]
