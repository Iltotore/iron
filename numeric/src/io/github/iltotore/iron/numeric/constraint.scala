package io.github.iltotore.iron.numeric

import io.github.iltotore.iron.==>
import io.github.iltotore.iron.constraint.Constraint

import scala.compiletime.{constValue, summonInline}

object constraint {

  trait Lesser[V]
  type <[A, B] = A ==> Lesser[B]
  type Negative[A] = Lesser[0]

  class LesserConstraint[A <: Number, V <: A] extends Constraint[A, Lesser[V]] {
    override inline def assert(value: A): Boolean = NumberOrdering.lt(value)
  }

  inline given[A <: Number, V <: A]: LesserConstraint[V] = new LesserConstraint


  trait Greater[V]
  type >[A, B] = A ==> Greater[B]
  type Positive[A] = Greater[0]
  type Natural = Int ==> Positive

  class GreaterConstraint[A <: Number, V <: A] extends Constraint[A, Greater[V]] {
    override inline def assert(value: A): Boolean = NumberOrdering.gt(value, constValue[V])
  }

  inline given[A <: Number, V <: A]: GreaterConstraint[A, V] = new GreaterConstraint

}