package io.github.iltotore.iron.string

import io.github.iltotore.iron.==>
import io.github.iltotore.iron.constraint.*

import scala.compiletime.constValue

object constraint {

  /**
   * Constraint: checks if the input value is lower case.
   */
  trait LowerCase

  inline given Constraint.RuntimeOnly[String, LowerCase] with {
    override inline def assert(value: String): Boolean = value equals value.toLowerCase

    override inline def getMessage(value: String): String = s"$value should be lower cased"
  }

  /**
   * Constraint: checks if the input value is upper case.
   */
  trait UpperCase

  inline given Constraint.RuntimeOnly[String, UpperCase] with {
    override inline def assert(value: String): Boolean = value equals value.toUpperCase

    override inline def getMessage(value: String): String = s"$value should be upper cased"
  }

  /**
   * Constraint: checks if the input value matches V.
   * @tparam V the regex to match with.
   */
  trait Match[V]

  type Alphanumeric = Match["^[a-zA-Z0-9]+"] DescribedAs "Value should be alphanumeric"

  type URLLike =
    Match["^(?:http(s)?:\\/\\/)?[\\w.-]+(?:\\.[\\w\\.-]+)+[\\w\\-\\._~:/?#[\\]@!\\$&'\\(\\)\\*\\+,;=.]+$"] DescribedAs "Value should be an URL"

  type UUIDLike =
    Match["^([0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12})"] DescribedAs "Value should be an UUID"

  inline given [V <: String]: Constraint.RuntimeOnly[String, Match[V]] with {
    override inline def assert(value: String): Boolean = constValue[V].r.matches(value)

    override inline def getMessage(value: String): String = s"$value should match ${constValue[V]}"
  }
}