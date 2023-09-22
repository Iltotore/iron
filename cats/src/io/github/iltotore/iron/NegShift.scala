package io.github.iltotore.iron

trait NegShift[A]:

  def shift(value: A): A

object NegShift:

  inline given NegShift[Int] with
    def shift(value: Int): Int = value | Int.MinValue
  inline given NegShift[Long] with
    def shift(value: Long): Long = value | Long.MinValue
  inline given NegShift[Float] with
    def shift(value: Float): Float = if value > 0 then Float.MinValue + value else value
  inline given NegShift[Double] with
    def shift(value: Double): Double = if value > 0 then Double.MinValue + value else value
