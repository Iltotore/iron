package io.github.iltotore.iron

import io.github.iltotore.iron.ordering.InlinedOrdering

package object numeric {

  /**
   * Represents a union of all numerical primitives.
   * This abstraction facilitates the creation of numerical constraints.
   */
  type Number = Byte | Short | Int | Long | Float | Double

  /**
   * A Number-supported modulo. Only supports non-floating primitives.
   * @param x the dividend
   * @param y the divisor
   * @return the remainder of the Euclidian Division
   */
  transparent inline def modulo(x: Number, y: Number): Int = inline x match {
    case a: Byte => inline y match {
      case b: Byte => a % b
    }
    case a: Short => inline y match {
      case b: Short => a % b
    }
    case a: Int => inline y match {
      case b: Int => a % b
    }
  }
}