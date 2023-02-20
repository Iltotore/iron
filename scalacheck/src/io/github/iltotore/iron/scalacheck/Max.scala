package io.github.iltotore.iron.scalacheck

trait Max[A]:

  def value: A

object Max:

  def apply[A](x: A): Max[A] = new Max[A]:

    def value: A = x

  given Max[Int] = Max(Int.MaxValue)
  given Max[Long] = Max(Long.MaxValue)
  given Max[Float] = Max(Float.MaxValue)
  given Max[Double] = Max(Double.MaxValue)

