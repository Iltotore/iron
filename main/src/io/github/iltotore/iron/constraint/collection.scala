package io.github.iltotore.iron.constraint

import io.github.iltotore.iron.Constraint
import io.github.iltotore.iron.ops.*

import scala.compiletime.constValue
import scala.compiletime.ops.string.Length

/**
 * Collection-related constraints.
 *
 * @see [[string]] which contains [[String]]-specific implementations for most of these constraints.
 */
object collection:

  /**
   * Tests minimum length. Supports [[Iterable]] and [[String]] by default.
   *
   * @tparam V the minimum length of the tested input
   */
  final class MinLength[V <: Int]

  inline given [V <: Int, I <: Iterable[?]]: Constraint[I, MinLength[V]] with

    override inline def test(value: I): Boolean = value.size >= constValue[V]

    override inline def message: String = "Should contain atleast " + stringValue[V] + " elements"

  /**
   * Tests maximum length. Supports [[Iterable]] and [[String]] by default.
   *
   * @tparam V the maximum length of the tested input
   */
  final class MaxLength[V <: Int]

  inline given [V <: Int, I <: Iterable[?]]: Constraint[I, MaxLength[V]] with

    override inline def test(value: I): Boolean = value.size <= constValue[V]

    override inline def message: String = "Should contain at most " + stringValue[V] + " elements"

  /**
   * Tests if the given collection contains a specific value.
   *
   * @tparam V the value the input must contain.
   */
  final class Contain[V]

  inline given [A, V <: A, I <: Iterable[A]]: Constraint[I, Contain[V]] with

    override inline def test(value: I): Boolean = value.iterator.contains(constValue[V])

    override inline def message: String = "Should contain at most " + stringValue[V] + " elements"
