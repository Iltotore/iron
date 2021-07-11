package io.github.iltotore.iron.iterable

import io.github.iltotore.iron.==>
import io.github.iltotore.iron.constraint.Constraint

import scala.compiletime.constValue
import scala.collection.Iterable

object constraint {

  trait MinSize[V]

  inline given [T, A <: Iterable[T], V <: Int]: Constraint.RuntimeOnly[A, MinSize[V]] with {
    override inline def assert(value: A): Boolean = value.size >= constValue[V]
  }

  trait MaxSize[V]

  inline given [T, A <: Iterable[T], V <: Int]: Constraint.RuntimeOnly[A, MaxSize[V]] with {
    override inline def assert(value: A): Boolean = value.size <= constValue[V]
  }

  trait Size[V]

  inline given [T, A <: Iterable[T], V <: Int]: Constraint.RuntimeOnly[A, Size[V]] with {
    override inline def assert(value: A): Boolean = value.size == constValue[V]
  }
}