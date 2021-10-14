package io.github.iltotore.iron.iterable

import io.github.iltotore.iron./
import io.github.iltotore.iron.constraint.Constraint

import scala.compiletime.constValue
import scala.collection.Iterable

object constraint {

  /**
   * Constraint: checks if the input has a size greater or equal to V
   * @tparam V
   */
  trait MinSize[V]

  inline given [T, A <: Iterable[T], V <: Int]: Constraint.RuntimeOnly[A, MinSize[V]] with {
    override inline def assert(value: A): Boolean = value.size >= constValue[V]

    override inline def getMessage(value: A): String = s"Length should be greater or equal to ${constValue[V]}"
  }

  /**
   * Constraint: checks if the input has a size lesser or equal to V
   * @tparam V
   */
  trait MaxSize[V]

  inline given [T, A <: Iterable[T], V <: Int]: Constraint.RuntimeOnly[A, MaxSize[V]] with {
    override inline def assert(value: A): Boolean = value.size <= constValue[V]

    override inline def getMessage(value: A): String = s"Length should be less or equal to ${constValue[V]}"
  }

  /**
   * Constraint: checks if the input has a size of V
   * @tparam V
   */
  trait Size[V]

  inline given [T, A <: Iterable[T], V <: Int]: Constraint.RuntimeOnly[A, Size[V]] with {
    override inline def assert(value: A): Boolean = value.size == constValue[V]

    override inline def getMessage(value: A): String = s"Length should equal to ${constValue[V]}"
  }

  type Empty = Size[0]

  /**
   * Constraint: checks if the input contains V
   * @tparam V
   */
  trait Contains[V]

  inline given [T, A <: Iterable[T], V <: T]: Constraint.RuntimeOnly[A, Contains[V]] with {
    override inline def assert(value: A): Boolean = value.iterator.contains(constValue[V])

    override inline def getMessage(value: A): String = s"Iterable should contain ${constValue[V]}"
  }
}