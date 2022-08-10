package io.github.iltotore

import scala.language.implicitConversions

package object iron:

  export constraints.*

  /** An Iron type (refined).
    * @tparam A
    *   the underlying type
    * @tparam C
    *   the predicate/constraint guarding this type
    */
  opaque type IronType[A, C] <: A = A
  type /[A, C] = IronType[A, C]

  object IronType:

    inline def apply[A, C](value: A): IronType[A, C] = value

  end IronType

  implicit inline def autoBoxValue[A, C](inline value: A)(using inline constraint: Constraint[A, C]): IronType[A, C] =
    macros.assertCondition(constraint.test(value), constraint.message)
    value
    
  implicit inline def autoCastIron[A, C1, C2, Impl <: Theorem[A, C1, C2]](inline value: A / C1)(using inline theorem: Impl): IronType[A, C2] =
    macros.assertCondition(theorem.test(value), theorem.message)
    value

end iron
