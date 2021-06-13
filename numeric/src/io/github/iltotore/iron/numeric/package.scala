package io.github.iltotore.iron

import io.github.iltotore.iron.ordering.InlinedOrdering

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

  inline def modulo(a: Number, b: Number): Int = inline n match {
    case a: Byte => inline y match {
      case b: Byte if a > b => 1
      case b: Byte if a == b => 0
      case b: Byte if a < b => -1
    }
    case a: Short => inline y match {
      case b: Short if a > b => 1
      case b: Short if a == b => 0
      case b: Short if a < b => -1
    }
    case a: Int => inline y match {
      case b: Int if a > b => 1
      case b: Int if a == b => 0
      case b: Int if a < b => -1
    }
    case a: Long => inline y match {
      case b: Long if a > b => 1
      case b: Long if a == b => 0
      case b: Long if a < b => -1
    }
    case a: Float => inline y match {
      case b: Float if a > b => 1
      case b: Float if a == b => 0
      case b: Float if a < b => -1
    }
    case a: Double => inline y match {
      case b: Double if a > b => 1
      case b: Double if a == b => 0
      case b: Double if a < b => -1
    }
  }
}