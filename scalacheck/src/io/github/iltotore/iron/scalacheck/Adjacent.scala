package io.github.iltotore.iron.scalacheck

trait Adjacent[A]:

  def nextUp(x: A): A

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