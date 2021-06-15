package io.github.iltotore.iron.numeric

import io.github.iltotore.iron.ordering.InlinedOrdering

implicit object NumberOrdering extends InlinedOrdering[Number] {

  override transparent inline def compare(inline x: Number, inline y: Number): Int = inline x match {
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
