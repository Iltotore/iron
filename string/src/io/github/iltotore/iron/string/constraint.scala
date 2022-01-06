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

  inline given MatchConstraint[Alphanumeric] with {}

  type URLLike =
    Match["^(?:http(s)?:\\/\\/)?[\\w.-]+(?:\\.[\\w\\.-]+)+[\\w\\-\\._~:/?#\\[\\]@!\\$&'\\(\\)\\*\\+,;=.]+$"] DescribedAs "Value should be an URL"

  inline given MatchConstraint[URLLike] with {}

  type UUIDLike =
    Match["^([0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12})"] DescribedAs "Value should be an UUID"

  inline given MatchConstraint[UUIDLike] with {}

  trait MatchConstraint[M <: Match[_] | DescribedAs[Match[_], _]] extends Constraint[String, M] {
    override inline def assert(value: String): Boolean = compileTime.checkMatch(value, compileTime.extractRegex[M]())

    override inline def getMessage(value: String): String = value + " should match " + compileTime.extractRegex[M]()
  }

  inline given [V <: String & Singleton](using NotGiven[MatchConstraint[Match[V]]]): Constraint.RuntimeOnly[String, Match[V]] with {
    override inline def assert(value: String): Boolean = constValue[V].r.matches(value)

    override inline def getMessage(value: String): String = s"$value should match ${constValue[V]}"
  }

}