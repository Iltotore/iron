package io.github.iltotore

import scala.annotation.implicitNotFound
import scala.language.implicitConversions
import scala.util.NotGiven

package object iron:

  export constraints.{*, given}

  /** An Iron type (refined).
    * @tparam A
    *   the underlying type
    * @tparam C
    *   the predicate/constraint guarding this type
    */
  opaque type IronType[A, C] <: A = A
  type :|[A, C] = IronType[A, C]

  object IronType:

    inline def apply[A, C](value: A): IronType[A, C] = value

  end IronType

  implicit inline def autoRefine[A, C](inline value: A)(using inline constraint: Constraint[A, C]): A :| C =
    macros.assertCondition(value, constraint.test(value), constraint.message)
    value

  @implicitNotFound("Could not prove that ${C1} implies ${C2}")
  final class Implication[C1, C2]
  type ==>[C1, C2] = Implication[C1, C2]

  implicit inline def autoCastIron[A, C1, C2](inline value: A :| C1)(using C1 ==> C2): A :| C2 = value

end iron
