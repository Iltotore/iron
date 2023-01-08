package io.github.iltotore.iron

trait PosShift[A]:

  def shift(value: A): A

object PosShift:

  inline given PosShift[Int] = value => value & Int.MaxValue
  inline given PosShift[Long] = value => value & Long.MaxValue
  inline given PosShift[Float] = value => if value < 0 then Float.MaxValue - value else value
  inline given PosShift[Double] = value => if value < 0 then Double.MaxValue - value else value