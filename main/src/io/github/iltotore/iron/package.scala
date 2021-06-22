package io.github.iltotore

import io.github.iltotore.iron.constraint.Constraint

import scala.compiletime.constValue


package object iron {

  /**
   * An opaque alias of A marked as "checked".
   * @tparam A the input/raw type
   * @tparam B the passed constraint's dummy
   */
  opaque type Constrained[A, B] <: A = A
  type ==>[A, B] = Constrained[A, B]

  object Constrained {

    /**
     * Public "constructor" for [[Constrained]].
     * @param value the value to be wrapped
     * @tparam A value's type
     * @tparam B the passed constraint's dummy
     * @return The [[Constrained]] version of [[value]]
     */
    def apply[A, B](value: A): Constrained[A, B] = value
  }
}