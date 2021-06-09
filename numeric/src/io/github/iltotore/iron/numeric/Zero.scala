package io.github.iltotore.iron.numeric

object Zero {

  /**
   * Represents the `Zero` value of a type.
   * @tparam T the represented type of this `Zero` instance.
   */
  opaque type Zero[T] <: T = T
  def apply[T](x: T): Zero[T] = x
  
}
