package io.github.iltotore.iron

trait NegShift[A]:

  def shift(value: A): A

object NegShift:

  inline given NegShift[Int] = value => value | Int.MinValue
  inline given NegShift[Long] = value => value | Long.MinValue
  inline given NegShift[Float] = value => if value > 0 then Float.MinValue + value else value
  inline given NegShift[Double] = value => if value > 0 then Double.MinValue + value else value