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


  trait LessEqual[V]

  type <=[A, B] = A ==> LessEqual[B]

  class LessEqualConstraint[A <: Number, V <: A] extends Constraint[A, LessEqual[V]] {
    override inline def assert(value: A): Boolean = NumberOrdering.lteq(value, constValue[V])
  }

  inline given[A <: Number, V <: A]: LessEqualConstraint[A, V] = new LessEqualConstraint


  trait Greater[V]

  type >[A, B] = A ==> Greater[B]
  type Natural1[T] = T > (T match {
    case Byte => GreaterEqual[0]
    case Short => GreaterEqual[0]
    case Int => GreaterEqual[0]
    case Long => GreaterEqual[0L]
  })

  class GreaterConstraint[A <: Number, V <: A] extends Constraint[A, Greater[V]] {
    override inline def assert(value: A): Boolean = NumberOrdering.gt(value, constValue[V])
  }

  inline given[A <: Number, V <: A]: GreaterConstraint[A, V] = new GreaterConstraint


  trait GreaterEqual[V]

  type >=[A, B] = A ==> GreaterEqual[B]
  type Natural[T] = T >= (T match {
    case Byte => GreaterEqual[0]
    case Short => GreaterEqual[0]
    case Int => GreaterEqual[0]
    case Long => GreaterEqual[0L]
  })

  class GreaterEqualConstraint[A <: Number, V <: A] extends Constraint[A, GreaterEqual[V]] {
    override inline def assert(value: A): Boolean = NumberOrdering.gteq(value, constValue[V])
  }

  inline given[A <: Number, V <: A]: GreaterEqualConstraint[A, V] = new GreaterEqualConstraint

  trait Divisible[V]

  type %[A, B] = A ==> Divisible[B]
  type Even[T] = T ==> (T match {
    case Byte => Divisible[2]
    case Short => Divisible[2]
    case Int => Divisible[2]
    case Long => Divisible[2L]
  })

  class DivisibleConstraint[A <: Number, V <: A] extends Constraint[A, Divisible[V]] {
    override inline def assert(value: A): Boolean = modulo(value, constValue[V]) == 0
  }

  inline given[A <: Number, V <: A]: DivisibleConstraint[A, V] = new DivisibleConstraint
}