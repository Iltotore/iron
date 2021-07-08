package io.github.iltotore.iron.string

import io.github.iltotore.iron.==>
import io.github.iltotore.iron.constraint.Constraint

import scala.compiletime.constValue

object constraint {

  trait LowerCase

  inline given Constraint.RuntimeOnly[String, LowerCase] with {
    override inline def assert(value: String): Boolean = value equals value.toLowerCase
  }

  trait UpperCase

  inline given Constraint.RuntimeOnly[String, UpperCase] with {
    override inline def assert(value: String): Boolean = value equals value.toUpperCase
  }

  trait Match[V]

  type Alphanumeric = Match["^[a-z0-9]+"]
  type URLLike = Match["^(?:http(s)?:\\/\\/)?[\\w.-]+(?:\\.[\\w\\.-]+)+[\\w\\-\\._~:/?#[\\]@!\\$&'\\(\\)\\*\\+,;=.]+$"]
  type UUIDLike = Match["^([0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12})"]

  inline given [V <: String]: Constraint.RuntimeOnly[String, Match[V]] with {
    override inline def assert(value: String): Boolean = constValue[V].r.matches(value)
  }
}