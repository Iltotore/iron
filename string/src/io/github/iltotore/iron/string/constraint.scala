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
}
