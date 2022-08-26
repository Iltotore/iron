package io.github.iltotore

import scala.annotation.implicitNotFound
import scala.Console.{CYAN, RESET}
import scala.compiletime.{codeOf, error}
import scala.language.implicitConversions
import scala.util.NotGiven

package object iron:

  export io.github.iltotore.iron.constraint.any.{*, given}

  /**
   * Union of all numerical primitives.
   * This abstraction facilitates the creation of numerical constraints.
   */
  type Number = Byte | Short | Int | Long | Float | Double

  type IntNumber = Byte | Short | Int | Long

  /**
   * An Iron type (refined).
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

  implicit inline def autoRefine[A, C](inline value: A)(using
      inline constraint: Constraint[A, C]
  ): A :| C =
    inline if !macros.isConstant(value) then macros.nonConstantError(value)
    macros.assertCondition(value, constraint.test(value), constraint.message)
    value

  @implicitNotFound("Could not prove that ${C1} implies ${C2}")
  final class Implication[C1, C2]

  type ==>[C1, C2] = Implication[C1, C2]

  implicit inline def autoCastIron[A, C1, C2](inline value: A :| C1)(using C1 ==> C2): A :| C2 = value

  extension [A](value: A)

    inline def refine[B](using inline constraint: Constraint[A, B]): A :| B =
      if constraint.test(value) then value
      else throw IllegalArgumentException(constraint.message)

    inline def refineEither[B](using inline constraint: Constraint[A, B]): Either[String, A :| B] =
      Either.cond(constraint.test(value), value, constraint.message)

    inline def refineOption[B](using inline constraint: Constraint[A, B]): Option[A :| B] =
      Option.when(constraint.test(value))(value)

end iron
