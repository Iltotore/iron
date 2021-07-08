package io.github.iltotore.iron.string

import io.github.iltotore.iron.==>
import io.github.iltotore.iron.constraint.Constraint

object constraint {

  trait LowerCase

  inline given Constraint.RuntimeOnly[String, LowerCase] with {
    override inline def assert(value: String): Boolean = value equals value.toLowerCase
  }
}
