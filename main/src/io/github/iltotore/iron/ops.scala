package io.github.iltotore.iron

import scala.compiletime.ops.*

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
}
