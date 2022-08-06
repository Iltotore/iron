package io.github.iltotore

import scala.language.implicitConversions

package object iron:

  export constraints.*

  /** An Iron type (refined).
    * @tparam T
    *   the underlying type
    * @tparam C
    *   the predicate/constraint guarding this type
    */
  opaque type IronType[T, C] <: T = T
  type /[T, C] = IronType[T, C]

  object IronType:

    inline def apply[T, C](value: T): IronType[T, C] = value

  end IronType

  implicit inline def autoBoxValue[T, C](inline value: T)(using inline constraint: Constraint[T, C]): IronType[T, C] =
    macros.assertCondition(constraint.test(value), constraint.message)
    IronType(value)

end iron
