package io.github.iltotore.iron.scalacheck

trait Min[A]:

  def value: A

object Min:

  def apply[A](x: A): Min[A] = new Min[A]:

    override def value: A = x

  given Min[Int] = Min(Int.MinValue)
  given Min[Long] = Min(Long.MinValue)
  given Min[Float] = Min(Float.MinValue)
  given Min[Double] = Min(Double.MinValue)

