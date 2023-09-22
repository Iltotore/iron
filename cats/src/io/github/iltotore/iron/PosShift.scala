package io.github.iltotore.iron

trait PosShift[A]:

  def shift(value: A): A

object PosShift:

  inline given PosShift[Int] with
    def shift(value: Int): Int = value & Int.MaxValue
  inline given PosShift[Long] with
    def shift(value: Long): Long = value & Long.MaxValue
  inline given PosShift[Float] with
    def shift(value: Float): Float = if value < 0 then Float.MaxValue - value else value
  inline given PosShift[Double] with
    def shift(value: Double): Double = if value < 0 then Double.MaxValue - value else value
