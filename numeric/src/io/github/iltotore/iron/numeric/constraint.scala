package io.github.iltotore.iron.numeric

import io.github.iltotore.iron.==>
import io.github.iltotore.iron.constraint.Constraint

import scala.compiletime.{constValue, summonInline}

object constraint {

  trait Less[V]
  type <[A, B] = A ==> Less[B]

  class LessConstraint[A <: Number, V <: A] extends Constraint[A, Less[V]] {
    override inline def assert(value: A): Boolean = NumberOrdering.lt(value, constValue[V])
  }

  inline given[A <: Number, V <: A]: LessConstraint[A, V] = new LessConstraint


  trait Greater[V]
  type >[A, B] = A ==> Greater[B]
  type Natural = Int > 0

  class GreaterConstraint[A <: Number, V <: A] extends Constraint[A, Greater[V]] {
    override inline def assert(value: A): Boolean = NumberOrdering.gt(value, constValue[V])
  }

  inline given[A <: Number, V <: A]: GreaterConstraint[A, V] = new GreaterConstraint

  trait Divisible[V]
  type %[A, B] = A ==> Divisible[B]
  type Even = Int ==> Divisible[2]

  class DivisibleConstraint[A <: Number, V <: A] extends Constraint[A, Divisible[V]] {
    override inline def assert(value: A): Boolean = modulo(value, constValue[V]) == 0
  }

  inline given[A <: Number, V <: A]: DivisibleConstraint[A, V] = new DivisibleConstraint
}