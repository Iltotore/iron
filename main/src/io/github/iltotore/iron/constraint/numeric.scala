package io.github.iltotore.iron.constraint

import io.github.iltotore.iron.{==>, Constraint, Implication, Number}
import io.github.iltotore.iron.ops.*
import io.github.iltotore.iron.ordering.NumberOrdering

import scala.compiletime.{constValue}
import scala.compiletime.ops.any.ToString

object numeric:

  final class Greater[V]

  inline given [A <: Number, V <: A]: Constraint[A, Greater[V]] with

    override inline def test(value: A): Boolean = NumberOrdering.gt(value, constValue[V])

    override inline def message: String = "Should be greater than " + compiletime.constValue[ToString[V]]

  inline given[V1, V2](using V1 > V2 =:= true): (Greater[V1] ==> Greater[V2]) = Implication()
