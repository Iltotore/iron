package io.github.iltotore.iron

import scala.compiletime.constValue
import scala.compiletime.ops.*, any.ToString

object ops {

  type Zero[A] = A match
    case Int => 0
    case Long => 0l
    case Float => 0f
    case Double => 0d


  type >[A, B] = (A, B) match
    case (Int, Int) => int.>[A, B]
    case (Int, Double) => double.>[int.ToDouble[A], B]
    case (Double, Int) => double.>[A, int.ToDouble[B]]
    case (Double, Double) => double.>[A, B]

  type >=[A, B] = (A, B) match
    case (Int, Int) => int.>=[A, B]
    case (Int, Double) => double.>=[int.ToDouble[A], B]
    case (Double, Int) => double.>=[A, int.ToDouble[B]]
    case (Double, Double) => double.>=[A, B]

  type <[A, B] = (A, B) match
    case (Int, Int) => int.<[A, B]
    case (Int, Double) => double.<[int.ToDouble[A], B]
    case (Double, Int) => double.<[A, int.ToDouble[B]]
    case (Double, Double) => double.<[A, B]

  type <=[A, B] = (A, B) match
    case (Int, Int) => int.<=[A, B]
    case (Int, Double) => double.<=[int.ToDouble[A], B]
    case (Double, Int) => double.<=[A, int.ToDouble[B]]
    case (Double, Double) => double.<=[A, B]

  type %[A, B] = (A, B) match
    case (Int, Int) => int.%[A, B]
    case (Long, Long) => long.%[A, B]

  type +[A, B] = (A, B) match
    case (Int, Int) => int.+[A, B]
    case (Int, Double) => double.+[int.ToDouble[A], B]
    case (Double, Int) => double.+[A, int.ToDouble[B]]
    case (Double, Double) => double.+[A, B]
    case (String, ?) => string.+[A, ToString[B]]
    case (?, String) => string.+[ToString[A], B]

  inline def stringValue[A]: String = constValue[ToString[A]]

  transparent inline def modulo(x: IntNumber, y: IntNumber): IntNumber = inline x match
    case a: Byte => inline y match
      case b: Byte => a % b
    case a: Short => inline y match
      case b: Short => a % b
    case a: Int => inline y match
      case b: Int => a % b
    case a: Long => inline y match
      case b: Long => a % b
}
