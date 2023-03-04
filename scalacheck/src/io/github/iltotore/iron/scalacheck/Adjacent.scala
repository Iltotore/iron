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

    override def nextUp(x: Int): Int = x + 1

    override def nextDown(x: Int): Int = x - 1

  given Adjacent[Long] with

    override def nextUp(x: Long): Long = x + 1

    override def nextDown(x: Long): Long = x - 1

  given Adjacent[Float] with

    override def nextUp(x: Float): Float = Math.nextUp(x)

    override def nextDown(x: Float): Float = Math.nextDown(x)

  given Adjacent[Double] with

    override def nextUp(x: Double): Double = Math.nextUp(x)

    override def nextDown(x: Double): Double = Math.nextDown(x)