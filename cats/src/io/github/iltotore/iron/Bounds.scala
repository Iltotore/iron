package io.github.iltotore.iron

import io.github.iltotore.iron.constraint.numeric.Greater
import io.github.iltotore.iron.constraint.numeric.*
import scala.compiletime.constValue
import scala.math.Numeric.Implicits.infixNumericOps
import scala.math.Numeric.IntIsIntegral
import scala.math.Ordering.Implicits.infixOrderingOps

/**
 * A way to shift out-of-bounds [[A]] values constrained with [[C]] constraint.
 *
 * @tparam A the base type
 * @tparam C the constraint type
 */
trait Bounds[A, C]:

  /**
   * Shift value if out of bounds.
   *
   * @param value the value to eventually shift
   * @return the passed value as is or shifted if necessary
   */
  def shift(value: A): A :| C

object Bounds:

  /**
   * Bounds for interval [L, U].
   *
   * @return
   */
  inline given closedBounds[A, L <: A, U <: A, C](using
      C ==> Interval.Closed[L, U],
      Interval.Closed[L, U] ==> C,
      Numeric[A]
  ): Bounds[A, C] =
    new:
      override def shift(value: A): A :| C =
        if value > constValue[U] then (value - constValue[U] + constValue[L] - summon[Numeric[A]].one).assume[C]
        else if value < constValue[L] then ((constValue[U]: A) + value - constValue[L] + summon[Numeric[A]].one).assume[C]
        else value.assume[C]

  // Positive
  given posIntBounds[C](using C ==> Positive, Positive ==> C): Bounds[Int, C] = value =>
    if value <= 0 then (value + Int.MaxValue).assume[C]
    else value.assume[C]

  given posLongBounds[C](using C ==> Positive, Positive ==> C): Bounds[Long, C] = value =>
    if value <= 0 then (value + Long.MaxValue).assume[Positive]
    else value.assume[C]

  given posFloatBounds[C](using C ==> Positive, Positive ==> C): Bounds[Float, C] = value =>
    if value <= 0 then (value + Float.MaxValue).assume[C]
    else value.assume[C]

  given posDoubleBounds[C](using C ==> Positive, Positive ==> C): Bounds[Double, C] = value =>
    if value <= 0 then (value + Double.MaxValue).assume[C]
    else value.assume[C]

  // Positive0
  given pos0IntBounds[C](using C ==> Positive0, Positive0 ==> C): Bounds[Int, C] = value =>
    if value < 0 then (value + Int.MaxValue + 1).assume[C]
    else value.assume[C]

  given pos0LongBounds[C](using C ==> Positive0, Positive0 ==> C): Bounds[Long, C] = value =>
    if value < 0 then (value + Long.MaxValue + 1).assume[C]
    else value.assume[C]

  given pos0FloatBounds[C](using C ==> Positive0, Positive0 ==> C): Bounds[Float, C] = value =>
    if value < 0 then (value + Float.MaxValue + 1).assume[C]
    else value.assume[C]

  given pos0DoubleBounds[C](using C ==> Positive0, Positive0 ==> C): Bounds[Double, C] = value =>
    if value < 0 then (value + Double.MaxValue + 1).assume[C]
    else value.assume[C]

  // Negative
  given negIntBounds[C](using C ==> Negative, Negative ==> C): Bounds[Int, C] = value =>
    if value >= 0 then (value + Int.MinValue).assume[C]
    else value.assume[C]

  given negLongBounds[C](using C ==> Negative, Negative ==> C): Bounds[Long, C] = value =>
    if value >= 0 then (value + Long.MinValue).assume[C]
    else value.assume[C]

  given negFloatBounds[C](using C ==> Negative, Negative ==> C): Bounds[Float, C] = value =>
    if value >= 0 then (value + Float.MinValue).assume[C]
    else value.assume[C]

  given negDoubleBounds[C](using C ==> Negative, Negative ==> C): Bounds[Double, C] = value =>
    if value >= 0 then (value + Double.MinValue).assume[C]
    else value.assume[C]

  // Negative
  given neg0IntBounds[C](using C ==> Negative0, Negative0 ==> C): Bounds[Int, C] = value =>
    if value > 0 then (value + Int.MinValue - 1).assume[C]
    else value.assume[C]

  given neg0LongBounds[C](using C ==> Negative0, Negative0 ==> C): Bounds[Long, C] = value =>
    if value > 0 then (value + Long.MinValue - 1).assume[C]
    else value.assume[C]

  given neg0FloatBounds[C](using C ==> Negative0, Negative0 ==> C): Bounds[Float, C] = value =>
    if value > 0 then (value + Float.MinValue - 1).assume[C]
    else value.assume[C]

  given neg0DoubleBounds[C](using C ==> Negative0, Negative0 ==> C): Bounds[Double, C] = value =>
    if value > 0 then (value + Double.MinValue - 1).assume[C]
    else value.assume[C]
