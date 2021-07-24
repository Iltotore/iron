package io.github.iltotore

import io.github.iltotore.iron.constraint.{Constraint, IllegalValueError}

import scala.compiletime.constValue
import scala.language.implicitConversions


package object iron {

  /**
   * Alias for Either[IllegalValueError[A], A], used as constraint result.
   * @tparam A the input type
   */
  type Refined[A] = Either[IllegalValueError[A], A]
  type RefinedField[A] = Either[IllegalValueError.Field, A]
  
  extension [A](refined: Refined[A]) {

    /**
     * Convert this value to its field-based version.
     * @param name the field name
     * @return the RefinedField of this value
     */
    def toField(name: String): RefinedField[A] = refined.left.map(_.toField(name))
  }

  /**
   * An alias of Refined marked as "checked".
   * @tparam A the input/raw type
   * @tparam B the passed constraint's dummy
   */
  opaque type Constrained[A, B] = Refined[A]
  type ==>[A, B] = Constrained[A, B]

  object Constrained {

    /**
     * Public "constructor" for [[Constrained]].
     * @param value the value to be wrapped
     * @tparam A value's type
     * @tparam B the passed constraint's dummy
     * @return The [[Constrained]] version of [[value]]
     */
    def apply[A, B](value: Refined[A]): Constrained[A, B] = value
  }

  /**
   * Implicit conversion from Constrained[A, B] to its shadowed type
   * @param constrained the Constrained to be cast from
   * @tparam A the input type
   * @tparam B the constraint's dummy
   * @return the Constrained as Refined[A]
   */
  implicit def constrainedToValue[A, B](constrained: Constrained[A, B]): Refined[A] = constrained
}