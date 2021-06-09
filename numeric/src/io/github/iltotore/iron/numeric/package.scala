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


  type NumericalPrimitive = Byte | Short | Int | Long | Float | Double

  transparent inline def transform(a: NumericalPrimitive): Any = inline a match {
    case x: Byte   => x
    case x: Short  => x
    case x: Int    => x
    case x: Long   => x
    case x: Float  => x
    case x: Double => x
    case _         => a
  }

}