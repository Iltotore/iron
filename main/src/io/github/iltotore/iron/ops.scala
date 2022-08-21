package io.github.iltotore.iron

import scala.compiletime.constValue
import scala.compiletime.ops.*, any.ToString

object ops {
  
  //TODO >=, <, <=

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

  type +[A, B] = (A, B) match
    case (Int, Int) => int.+[A, B]
    case (Int, Double) => double.+[int.ToDouble[A], B]
    case (Double, Int) => double.+[A, int.ToDouble[B]]
    case (Double, Double) => double.+[A, B]
    case (String, ?) => string.+[A, ToString[B]]
    case (?, String) => string.+[ToString[A], B]
  
  inline def stringValue[A]: String = constValue[ToString[A]]
}
