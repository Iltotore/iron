package io.github.iltotore.iron

import scala.compiletime.constValue
import scala.compiletime.ops.*
import scala.compiletime.ops.any.ToString
import scala.quoted.*

/**
 * Methods and types to ease compile-time operations.
 */
object compileTime:

  type NumConstant = Int | Long | Float | Double

  /**
   * The zero number of the given type.
   * @tparam A the numerical primitive type.
   */
  type Zero[A] = A match
    case Int    => 0
    case Long   => 0L
    case Float  => 0f
    case Double => 0d

  /**
   * Convert the two given numerical types to the least common parent.
   *
   * @tparam A the first type to convert.
   * @tparam B the second type to convert.
   */
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

  /**
   * Polymorphic numerical binary operator. Takes the right implementation according to the least common type of `A` and `B`.
   *
   * @tparam A the left member of this operation.
   * @tparam B the right member of this operation.
   * @tparam IntOp the operation to apply if both types can be converted to `Int`.
   * @tparam LongOp the operation to apply if both types can be converted to `Long`.
   * @tparam FloatOp the operation to apply if both types can be converted to `Float`.
   * @tparam DoubleOp the operation to apply if both types can be converted to `Double`.
   */
  type NumOp[A, B, IntOp[_ <: Int, _ <: Int], LongOp[_ <: Long, _ <: Long], FloatOp[_ <: Float, _ <: Float], DoubleOp[_ <: Double, _ <: Double]] =
    Compatible[A, B] match
      case (Int, Int)       => IntOp[A, B]
      case (Long, Long)     => LongOp[A, B]
      case (Float, Float)   => FloatOp[A, B]
      case (Double, Double) => DoubleOp[A, B]

  /**
   * Polymorphic strict superiority.
   *
   * @tparam A the left member of this operation.
   * @tparam B the right member of this operation.
   */
  type >[A, B] = NumOp[A, B, int.>, long.>, float.>, double.>]

  /**
   * Polymorphic non-strict superiority.
   *
   * @tparam A the left member of this operation.
   * @tparam B the right member of this operation.
   */
  type >=[A, B] = NumOp[A, B, int.>=, long.>=, float.>=, double.>=]

  /**
   * Polymorphic strict inferiority.
   *
   * @tparam A the left member of this operation.
   * @tparam B the right member of this operation.
   */
  type <[A, B] = NumOp[A, B, int.<, long.<, float.<, double.<]

  /**
   * Polymorphic non-strict inferiority.
   *
   * @tparam A the left member of this operation.
   * @tparam B the right member of this operation.
   */
  type <=[A, B] = NumOp[A, B, int.<=, long.<=, float.<=, double.<=]

  /**
   * Polymorphic addition.
   *
   * @tparam A the left member of this operation.
   * @tparam B the right member of this operation.
   */
  type +[A, B] = (A, B) match
    case (String, ?) => string.+[A, ToString[B]]
    case (?, String) => string.+[ToString[A], B]
    case _           => NumOp[A, B, int.+, long.+, float.+, double.+]

  /**
   * Polymorphic strict subtraction.
   *
   * @tparam A the left member of this operation.
   * @tparam B the right member of this operation.
   */
  type -[A, B] = NumOp[A, B, int.-, long.-, float.-, double.-]

  /**
   * Polymorphic multiplication.
   *
   * @tparam A the left member of this operation.
   * @tparam B the right member of this operation.
   */
  type *[A, B] = NumOp[A, B, int.*, long.*, float.*, double.*]

  /**
   * Polymorphic division.
   *
   * @tparam A the left member of this operation.
   * @tparam B the right member of this operation.
   */
  type /[A, B] = NumOp[A, B, int./, long./, float./, double./]

  /**
   * Polymorphic modulo.
   *
   * @tparam A the left member of this operation.
   * @tparam B the right member of this operation.
   */
  type %[A, B] = NumOp[A, B, int.%, long.%, float.%, double.%]

  /**
   * Polymorphic `ToDouble`.
   *
   * @tparam A the constant type to cast.
   */
  type ToDouble[A <: NumConstant] <: Double = A match
    case Int    => int.ToDouble[A]
    case Long   => long.ToDouble[A]
    case Float  => float.ToDouble[A]
    case Double => A & Double

  /**
   * Polymorphic `ToLong`.
   *
   * @tparam A the constant type to cast.
   */
  type ToLong[A <: NumConstant] <: Long = A match
    case Double => double.ToLong[A]
    case Float  => float.ToLong[A]
    case Int    => int.ToLong[A]
    case Long   => A & Long

  /**
   * Get the `Double` value of the given type.
   *
   * @tparam A the type to convert to `Double`.
   * @return the String representation of the given type. Equivalent to `constValue[ToDouble[A]]`.
   */
  transparent inline def doubleValue[A <: NumConstant]: Double = constValue[ToDouble[A]]

  /**
   * Get the `Long` value of the given type.
   *
   * @tparam A the type to convert to `Long`.
   * @return the Long representation of the given type. Equivalent to `constValue[ToLong[A]]`.
   */
  transparent inline def longValue[A <: NumConstant]: Long = constValue[ToLong[A]]

  /**
   * Get the `String` value of the given type.
   *
   * @tparam A the type to convert to `String`.
   * @return the String representation of the given type. Equivalent to `constValue[ToString[A]]`.
   */
  transparent inline def stringValue[A]: String = constValue[ToString[A]]

  def applyConstraint[A, C, Impl <: Constraint[A, C]](expr: Expr[A], constraintExpr: Expr[Impl])(using
      Quotes
  ): Expr[Boolean] = // Using quotes directly causes a "deferred inline error"

    import quotes.reflect.*

    Apply(Select.unique(constraintExpr.asTerm, "test"), List(expr.asTerm)).asExprOf[Boolean]
