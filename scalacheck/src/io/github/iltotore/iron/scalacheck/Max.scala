package io.github.iltotore.iron.scalacheck

/**
 * Represent the maximum value of a type.
 * @tparam A
 */
trait Max[A]:

  /**
   * The greatest value of type `A`
   */
  def value: A

object Max:

  def apply[A](x: A): Max[A] = new Max[A]:

    override def value: A = x

  given Max[Int] = Max(Int.MaxValue)
  given Max[Long] = Max(Long.MaxValue)
  given Max[Float] = Max(Float.MaxValue)
  given Max[Double] = Max(Double.MaxValue)

