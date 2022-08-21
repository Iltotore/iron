package io.github.iltotore.iron.constraint

import io.github.iltotore.iron.{==>, Constraint, Implication, Number}
import io.github.iltotore.iron.ops.*
import io.github.iltotore.iron.constraint.any.{*, given}
import io.github.iltotore.iron.ordering.NumberOrdering

import scala.compiletime.constValue
import scala.compiletime.ops.any.ToString

object numeric:

  //TODO GreaterEqual, Less, LessEqual, Multiple

  final class Greater[V]

  inline given [A <: Number, V <: A]: Constraint[A, Greater[V]] with

    override inline def test(value: A): Boolean = NumberOrdering.gt(value, constValue[V])

    override inline def message: String = "Should be greater than " + stringValue[V]

  inline given[V1, V2](using V1 > V2 =:= true): (Greater[V1] ==> Greater[V2]) = Implication()
  inline given[V1, V2](using V1 > V2 =:= true): (StrictEqual[V1] ==> Greater[V2]) = Implication()


  type GreaterEqual[V] = (Greater[V] || StrictEqual[V]) DescribedAs ("Should be greater than or equal to " + V)