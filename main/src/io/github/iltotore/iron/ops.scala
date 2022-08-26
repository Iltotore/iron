package io.github.iltotore.iron

import scala.compiletime.constValue
import scala.compiletime.ops.*, any.ToString

object ops:

  type Zero[A] = A match
    case Int    => 0
    case Long   => 0L
    case Float  => 0f
    case Double => 0d

  type Compatible[A, B] = A match
    case Int =>
      B match
        case Int    => (A, B)
        case Long   => (int.ToLong[A], B)
        case Float  => (int.ToFloat[A], B)
        case Double => (int.ToDouble[A], B)
    case Long =>
      B match
        case Int    => (A, int.ToLong[B])
        case Long   => (A, B)
        case Float  => (long.ToFloat[A], B)
        case Double => (long.ToDouble[A], B)
    case Float =>
      B match
        case Int    => (A, int.ToFloat[A])
        case Long   => (A, long.ToFloat[A])
        case Float  => (A, B)
        case Double => (float.ToDouble[A], B)
    case Double =>
      B match
        case Int    => (A, int.ToDouble[A])
        case Long   => (A, long.ToDouble[A])
        case Float  => (A, float.ToDouble[A])
        case Double => (A, B)

  type NumOp[A, B, IntOp[_ <: Int, _ <: Int], LongOp[_ <: Long, _ <: Long], FloatOp[_ <: Float, _ <: Float], DoubleOp[_ <: Double, _ <: Double]] =
    Compatible[A, B] match
      case (Int, Int)       => IntOp[A, B]
      case (Long, Long)     => LongOp[A, B]
      case (Float, Float)   => FloatOp[A, B]
      case (Double, Double) => DoubleOp[A, B]

  type >[A, B] = NumOp[A, B, int.>, long.>, float.>, double.>]
  type >=[A, B] = NumOp[A, B, int.>=, long.>=, float.>=, double.>=]
  type <[A, B] = NumOp[A, B, int.<, long.<, float.<, double.<]
  type <=[A, B] = NumOp[A, B, int.<=, long.<=, float.<=, double.<=]

  type +[A, B] = (A, B) match
    case (String, ?) => string.+[A, ToString[B]]
    case (?, String) => string.+[ToString[A], B]
    case _           => NumOp[A, B, int.+, long.+, float.+, double.+]

  type -[A, B] = NumOp[A, B, int.-, long.-, float.-, double.-]
  type *[A, B] = NumOp[A, B, int.*, long.*, float.*, double.*]
  type /[A, B] = NumOp[A, B, int./, long./, float./, double./]
  type %[A, B] = NumOp[A, B, int.%, long.%, float.%, double.%]

  inline def stringValue[A]: String = constValue[ToString[A]]

  transparent inline def modulo(x: IntNumber, y: IntNumber): IntNumber = inline x match
    case a: Byte =>
      inline y match
        case b: Byte => a % b
    case a: Short =>
      inline y match
        case b: Short => a % b
    case a: Int =>
      inline y match
        case b: Int => a % b
    case a: Long =>
      inline y match
        case b: Long => a % b
