package io.github.iltotore.iron

import io.github.iltotore.iron.ordering.InlinedOrdering

package object numeric {

  type Number = Byte | Short | Int | Long | Float | Double

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