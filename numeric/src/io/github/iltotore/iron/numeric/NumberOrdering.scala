package io.github.iltotore.iron.numeric

import io.github.iltotore.iron.ordering.InlinedOrdering

implicit object NumberOrdering extends InlinedOrdering[Number] {

  override transparent inline def compare(inline x: Number, inline y: Number): Int = inline x match {
    case a: Byte => inline y match {
      case b: Byte => a % b
    }
    case a: Short => inline y match {
      case b: Short => a % b
    }
    case a: Int => inline y match {
      case b: Int => a % b
    }
    case a: Long => inline y match {
      case b: Long => a % b
    }
    case a: Float => inline y match {
      case b: Float => a % b
    }
    case a: Double => inline y match {
      case b: Double => a % b
    }
  }
}
