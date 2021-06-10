package io.github.iltotore.iron

package object numeric {

  type Zero[T] = Zero.Zero[T]

  inline given Zero[Byte] = Zero(0)
  inline given Zero[Short] = Zero(0)
  inline given Zero[Int] = Zero(0)
  inline given Zero[Long] = Zero(0)
  inline given Zero[Float] = Zero(0f)
  inline given Zero[Double] = Zero(0d)
  inline given Zero[BigInt] = Zero(BigInt(0))
  inline given Zero[BigDecimal] = Zero(BigDecimal(0))


  type Number = Byte | Short | Int | Long | Float | Double

}