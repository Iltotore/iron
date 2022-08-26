package io.github.iltotore.iron.constraint

import io.github.iltotore.iron.Constraint
import io.github.iltotore.iron.ops.*

import scala.compiletime.constValue
import scala.compiletime.ops.string.Length

object collection:

  final class MinLength[V <: Int]

  inline given [V <: Int, I <: Iterable[?]]: Constraint[I, MinLength[V]] with

    override inline def test(value: I): Boolean = value.size >= constValue[V]

    override inline def message: String = "Should contain atleast " + stringValue[V] + " elements"

  final class MaxLength[V <: Int]

  inline given [V <: Int, I <: Iterable[?]]: Constraint[I, MaxLength[V]] with

    override inline def test(value: I): Boolean = value.size <= constValue[V]

    override inline def message: String = "Should contain at most " + stringValue[V] + " elements"

  final class Contain[V]

  inline given [A, V <: A, I <: Iterable[A]]: Constraint[I, Contain[V]] with

    override inline def test(value: I): Boolean = value.iterator.contains(constValue[V])

    override inline def message: String = "Should contain at most " + stringValue[V] + " elements"
