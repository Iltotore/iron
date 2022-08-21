package io.github.iltotore.iron.ordering

import io.github.iltotore.iron.Number

/**
 * An InlineOrdering instance for Number
 */
given NumberOrdering: InlinedOrdering[Number] with

  override transparent inline def compare(inline x: Number, inline y: Number): Int = inline x match
    case a: Byte => inline y match
        case b: Byte if a > b => 1
        case b: Byte if a == b => 0
        case b: Byte if a < b => -1
        case b: Byte => Ordering.Byte.compare(a, b) // This "default" case allows runtime evaluation
    case a: Short => inline y match
        case b: Short if a > b => 1
        case b: Short if a == b => 0
        case b: Short if a < b => -1
        case b: Short => Ordering.Short.compare(a, b)
    case a: Int => inline y match
        case b: Int if a > b => 1
        case b: Int if a == b => 0
        case b: Int if a < b => -1
        case b: Int => Ordering.Int.compare(a, b)
    case a: Long => inline y match
        case b: Long if a > b => 1
        case b: Long if a == b => 0
        case b: Long if a < b => -1
        case b: Long => Ordering.Long.compare(a, b)
    case a: Float => inline y match
        case b: Float if a > b => 1
        case b: Float if a == b => 0
        case b: Float if a < b => -1
        case b: Float => Ordering.Float.TotalOrdering.compare(a, b)
    case a: Double => inline y match
        case b: Double if a > b => 1
        case b: Double if a == b => 0
        case b: Double if a < b => -1
        case b: Double => Ordering.Double.TotalOrdering.compare(a, b)
