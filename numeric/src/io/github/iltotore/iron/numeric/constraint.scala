package io.github.iltotore.iron.numeric

import io.github.iltotore.iron.==>
import io.github.iltotore.iron.constraint.Constraint

import scala.compiletime.{constValue, summonInline}

object constraint {

  trait Lesser[V]
  type <[A, B] = A ==> Lesser[B]
  type Negative[A] = A ==> Lesser[0]

  class LesserConstraint[A <: Double, V <: Double] extends Constraint[A, Lesser[V]] {
    override inline def assert(value: A): Boolean = value < constValue[V]
  }

  inline given[A <: Double, V <: Double]: LesserConstraint[A, V] = new LesserConstraint


  trait Greater[V]
  type >[A, B] = A ==> Greater[B]
  type Positive[A] = A ==> Greater[0]

  class GreaterConstraint[A <: Double, V <: Double] extends Constraint[A, Greater[V]] {
    override inline def assert(value: A): Boolean = value > constValue[V]
  }

  inline given[A <: Double, V <: Double]: GreaterConstraint[A, V] = new GreaterConstraint


}
