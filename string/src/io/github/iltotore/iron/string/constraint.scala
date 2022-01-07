package io.github.iltotore.iron.string

import io.github.iltotore.iron./
import io.github.iltotore.iron.constraint.*

import scala.compiletime.{constValue, constValueOpt, erasedValue}
import scala.util.NotGiven

object constraint {

  /**
   * Constraint: checks if the input value is lower case.
   */
  trait LowerCase

  inline given Constraint[String, LowerCase] with {
    override inline def assert(value: String): Boolean = compileTime.checkLowerCase(value)

    override inline def getMessage(value: String): String = value + " should be lower cased"
  }

  /**
   * Constraint: checks if the input value is upper case.
   */
  trait UpperCase

  inline given Constraint[String, UpperCase] with {
    override inline def assert(value: String): Boolean = compileTime.checkUpperCase(value)

    override inline def getMessage(value: String): String = value + " should be upper cased"
  }

  /**
   * Constraint: checks if the input value matches V.
   * @tparam V the regex to match with.
   */
  trait Match[V <: String & Singleton]

  type Alphanumeric = Match["^[a-zA-Z0-9]+"] DescribedAs "Value should be alphanumeric"

  type URLLike =
    Match["^(?:http(s)?:\\/\\/)?[\\w.-]+(?:\\.[\\w\\.-]+)+[\\w\\-\\._~:/?#\\[\\]@!\\$&'\\(\\)\\*\\+,;=.]+$"] DescribedAs "Value should be an URL"

  type UUIDLike =
    Match["^([0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12})"] DescribedAs "Value should be an UUID"

  class MatchConstraint[V <: String & Singleton] extends Constraint[String, Match[V]] {

    override inline def assert(value: String): Boolean = compileTime.checkMatch(value, constValue[V])

    override inline def getMessage(value: String): String = value + " should match " + constValue[V]
  }

  inline given [V <: String & Singleton]: MatchConstraint[V] = new MatchConstraint

}