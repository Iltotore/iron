package io.github.iltotore.iron.numeric

import io.github.iltotore.iron.==>
import io.github.iltotore.iron.constraint.Constraint

import scala.compiletime.{constValue, summonInline}

object constraint {

  trait Lesser[V]
  type <[A, B] = A ==> Lesser[B]
  type Negative[A] = A ==> Lesser[0]

  class LesserConstraint[A <: Number, V <: Number] extends Constraint[Number, Lesser[V]] {
    override inline def assert(value: Double): Boolean = value < constValue[V]
  }

  inline given[V <: Double]: LesserConstraint[V] = new LesserConstraint


  trait Greater[V]
  type >[A, B] = A ==> Greater[B]
  type Positive[A] = A ==> Greater[0]

  class GreaterConstraint[A <: Number, V <: A] extends Constraint[A, Greater[V]] {
    override inline def assert(value: A): Boolean = NumberOrdering.gt(value, constValue[V])
  }

  inline given[A <: Number, V <: A]: GreaterConstraint[A, V] = new GreaterConstraint


}