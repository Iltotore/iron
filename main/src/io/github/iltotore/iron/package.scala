package io.github.iltotore

import io.github.iltotore.iron.constraint.{Consequence, Constraint, IllegalValueError, Literal}

import scala.compiletime.constValue
import scala.language.implicitConversions


package object iron {

  /**
   * Alias for Either[IllegalValueError[A], A], used as constraint result.
   *
   * @tparam A the input type
   */
  type Refined[A] = Either[IllegalValueError[A], A]
  type RefinedField[A] = Either[IllegalValueError.Field, A]

  extension[A] (refined: Refined[A]) {

    /**
     * Convert this value to its field-based version.
     *
     * @param name the field name
     * @return the RefinedField of this value
     */
    def toField(name: String): RefinedField[A] = refined.left.map(_.toField(name))
  }

  /**
   * An alias of Refined marked as "checked".
   *
   * @tparam A the input/raw type
   * @tparam B the passed constraint's dummy
   */
  opaque type Constrained[A, B] = Either[IllegalValueError[A], A]

  @deprecated("Use the `A / B` alias", "1.1.2")
  type ==>[A, B] = Constrained[A, B]
  type /[A, B] = Constrained[A, B]

  /**
   * Represent a refinement-less Constrained. This is different from [[Refined]] since Raw can be used as a Constrained
   * while [[Refined]] is a standard `Either`.
   * @tparam A the value's type
   */
  type Raw[A] = A / Literal[true]

  object Constrained {

    /**
     * Public "constructor" for [[Constrained]].
     *
     * @param value the value to be wrapped
     * @tparam A value's type
     * @tparam B the passed constraint's dummy
     * @return The [[Constrained]] version of value
     */
    inline def apply[A, B](value: Refined[A]): Constrained[A, B] = value
  }

  /**
   * Empty object implicitly available in a refined block.
   */
  case object RefinedDSL

  /**
   * Capability for constrained values. Allow usage of constrained values in an imperative style.
   * {{{
   *   //Vanilla version
   *   def log(x: Double > 0d): Refined[Double] = x.map(Math.log)
   *
   *   //Imperative version
   *   def log(x: Double > 0d): Raw[Double] = refined {
   *     Math.log(x)
   *   }
   * }}}
   *
   * @param statement the imperative-styled block
   * @tparam A the input value type
   * @tparam B the constraint's dummy
   * @return the resulting constraint of the passed statement
   * @see [[Raw]]
   */
  inline def refined[A, B](statement: RefinedDSL.type ?=> Constrained[A, B]): Constrained[A, B] =
    try {
      statement(using RefinedDSL)
    } catch case err: IllegalValueError[A] => Constrained(Left(err))

  /**
   * Abort the current refinement and return an IllegalValueError instead.
   *
   * @param input   the invalid value
   * @param message the assertion message
   * @tparam A the input value type
   */
  inline def reject[A](input: A, message: String)(using RefinedDSL.type): Nothing = throw IllegalValueError(input, message)

  /**
   * Unbox the given Constrained.
   *
   * @param constrained the constrained to unwrap
   * @tparam A the input value type
   * @tparam B the constraint's dummy
   * @return the underlying value
   */
  implicit def unbox[A, B](constrained: A / B)(using RefinedDSL.type): A = constrained.fold(throw _, x => x)
  
  implicit inline def refineConstrained[A, B1, B2](constrained: A / B1)(using inline consequence: Consequence[A, B1, B2]): A / B2 = constrained match {

    case Right(value) => Constrained(compileTime.preAssert(value, consequence.getMessage(value), consequence.assert(value)))

    case left => Constrained(left)
  }

  /**
   * Implicit conversion from Constrained[A, B] to its shadowed type.
   *
   * @param constrained the Constrained to be cast from
   * @tparam A the input type
   * @tparam B the constraint's dummy
   * @return the Constrained as Refined[A]
   */
  implicit inline def constrainedToValue[A, B](constrained: Constrained[A, B]): Refined[A] = constrained
}