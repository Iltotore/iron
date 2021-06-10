package io.github.iltotore.iron.numeric

import io.github.iltotore.iron.==>
import io.github.iltotore.iron.constraint.Constraint

import scala.compiletime.{constValue, summonInline}

object constraint {

  trait Lesser[V]
  type <[A, B] = A ==> Lesser[B]
  type Negative[A] = A ==> Lesser[0]

  class LesserConstraint[V <: Double] extends Constraint[Double, Lesser[V]] {
    override inline def assert(value: Double): Boolean = value < constValue[V]
  }

  inline given[V <: Double]: LesserConstraint[V] = new LesserConstraint


  trait Greater[V]
  type >[A, B] = A ==> Greater[B]
  type Positive[A] = A ==> Greater[0]

  class GreaterConstraint[V <: Double] extends Constraint[Double, Greater[V]] {
    override inline def assert(value: Double): Boolean = value > constValue[V]
  }

  inline given[V <: Double]: GreaterConstraint[V] = new GreaterConstraint


}
