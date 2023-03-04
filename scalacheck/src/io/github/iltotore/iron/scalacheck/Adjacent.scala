package io.github.iltotore.iron.scalacheck

/**
 * Adjacent values for a type.
 * @tparam A
 */
trait Adjacent[A]:

  /**
   * Get the next value.
   * @param x the value to shift up.
   * @return the least value greater than x.
   */
  def nextUp(x: A): A

  /**
   * Get the down value.
   * @param x the value to shift down.
   * @return the greatest value lower than x.
   */
  def nextDown(x: A): A

object Adjacent:

  given Adjacent[Int] with

    override def nextUp(x: Int): Int = if x == Int.MaxValue then x else x + 1

    override def nextDown(x: Int): Int = if x == Int.MinValue then x else x - 1

  given Adjacent[Long] with

    override def nextUp(x: Long): Long = if x == Long.MaxValue then x else x + 1

    override def nextDown(x: Long): Long = if x == Long.MinValue then x else x - 1

  given Adjacent[Float] with

    override def nextUp(x: Float): Float = if x == Float.MaxValue then x else Math.nextUp(x)

    override def nextDown(x: Float): Float = if x == Float.MinValue then x else Math.nextDown(x)

  given Adjacent[Double] with

    override def nextUp(x: Double): Double = if x == Double.MaxValue then x else Math.nextUp(x)

    override def nextDown(x: Double): Double = Math.nextDown(x)