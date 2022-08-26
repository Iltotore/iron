package io.github.iltotore.iron.constraint

import io.github.iltotore.iron.{==>, Constraint, Implication, IntNumber, Number}
import io.github.iltotore.iron.ops.*
import io.github.iltotore.iron.constraint.any.{*, given}
import io.github.iltotore.iron.ordering.NumberOrdering

import scala.compiletime.constValue
import scala.compiletime.ops.any.ToString

object numeric:

  final class Greater[V]

  inline given [A <: Number, V <: A]: Constraint[A, Greater[V]] with

    override inline def test(value: A): Boolean = NumberOrdering.gt(value, constValue[V])

    override inline def message: String = "Should be greater than " + stringValue[V]

  inline given [V1, V2](using V1 > V2 =:= true): (Greater[V1] ==> Greater[V2]) = Implication()
  inline given [V1, V2](using V1 > V2 =:= true): (StrictEqual[V1] ==> Greater[V2]) = Implication()

  type GreaterEqual[V] = (Greater[V] || StrictEqual[V]) DescribedAs ("Should be greater than or equal to " + V)

  final class Less[V]

  inline given [A <: Number, V <: A]: Constraint[A, Less[V]] with

    override inline def test(value: A): Boolean = NumberOrdering.lt(value, constValue[V])

    override inline def message: String = "Should be less than " + stringValue[V]

  inline given [V1, V2](using V1 < V2 =:= true): (Less[V1] ==> Less[V2]) = Implication()

  inline given [V1, V2](using V1 < V2 =:= true): (StrictEqual[V1] ==> Less[V2]) = Implication()

  type LessEqual[V] = (Less[V] || StrictEqual[V]) DescribedAs ("Should be less than or equal to " + V)

  final class Multiple[V]

  inline given [A <: IntNumber, V <: A]: Constraint[A, Multiple[V]] with

    override inline def test(value: A): Boolean = modulo(value, constValue[V]) == 0

    override inline def message: String = "Should be a multiple of " + stringValue[V]

  given [A, V1 <: A, V2 <: A](using V1 % V2 =:= Zero[A]): (Multiple[V1] ==> Multiple[V2]) = Implication()

  final class Divide[V]

  inline given [A <: IntNumber, V <: A]: Constraint[A, Divide[V]] with

    override inline def test(value: A): Boolean = modulo(constValue[V], value) == 0

    override inline def message: String = "Should divide " + stringValue[V]
