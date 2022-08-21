package io.github.iltotore.iron.constraint

import io.github.iltotore.iron.{==>, Constraint, Implication}

import scala.compiletime.{constValue, summonInline}
import scala.compiletime.ops.any.ToString
import scala.compiletime.ops.boolean

object any:

  given [C]: (C ==> C) = Implication()

  final class DescribedAs[C, V <: String]

  class DescribedAsConstraint[A, C, Impl <: Constraint[A, C], V <: String](using Impl)
      extends Constraint[A, DescribedAs[C, V]]:

    override inline def test(value: A): Boolean = summonInline[Impl].test(value)

    override inline def message: String = constValue[V]

  transparent inline given [A, C, Impl <: Constraint[A, C], V <: String](using
      inline constraint: Impl
  ): Constraint[A, DescribedAs[C, V]] =
    new DescribedAsConstraint


  final class Not[C]
  type ![C] = C match
    case Boolean => boolean.![C]
    case _ => Not[C]

  class NotConstraint[A, C, Impl <: Constraint[A, C]](using Impl) extends Constraint[A, Not[C]]:

    override inline def test(value: A): Boolean =
      !summonInline[Impl].test(value)

    override inline def message: String =
      "!(" + summonInline[Impl].message + ")"

  transparent inline given [A, C, Impl <: Constraint[A, C]](using
      inline constraint: Impl
  ): Constraint[A, Not[C]] = new NotConstraint

  given [C1, C2](using C1 ==> C2): (Not[Not[C1]] ==> C2) = Implication()
  given [C1, C2](using C1 ==> C2): (C1 ==> Not[Not[C2]]) = Implication()


  final class Or[C1, C2]
  type ||[C1, C2] = (C1, C2) match
    case (Boolean, Boolean) => boolean.||[C1, C2]
    case _ => Or[C1, C2]

  class OrConstraint[A, C1, C2, Impl1 <: Constraint[A, C1], Impl2 <: Constraint[A, C2]](using Impl1, Impl2)
      extends Constraint[A, Or[C1, C2]]:

    override inline def test(value: A): Boolean =
      summonInline[Impl1].test(value) || summonInline[Impl2].test(value)

    override inline def message: String =
      "(" + summonInline[Impl1].message + ") || (" + summonInline[
        Impl2
      ].message + ")"

  transparent inline given [A, C1, C2, Impl1 <: Constraint[A, C1], Impl2 <: Constraint[A, C2]](using
      inline left: Impl1,
      inline right: Impl2
  ): Constraint[A, Or[C1, C2]] = new OrConstraint

  given [C1, C2, C3](using (C1 ==> C2) | (C1 ==> C3)): (C1 ==> Or[C2, C3]) =
    Implication()


  final class And[C1, C2]

  type &&[C1, C2] = (C1, C2) match
    case (Boolean, Boolean) => boolean.&&[C1, C2]
    case _ => And[C1, C2]

  class AndConstraint[A, C1, C2, Impl1 <: Constraint[A, C1], Impl2 <: Constraint[A, C2]](using Impl1, Impl2)
      extends Constraint[A, And[C1, C2]]:

    override inline def test(value: A): Boolean =
      summonInline[Impl1].test(value) && summonInline[Impl2].test(value)

    override inline def message: String =
      "(" + summonInline[Impl1].message + ") && (" + summonInline[Impl2].message + ")"

  transparent inline given [A, C1, C2, Impl1 <: Constraint[A, C1], Impl2 <: Constraint[A, C2]](using
      inline left: Impl1,
      inline right: Impl2
  ): Constraint[A, And[C1, C2]] = new AndConstraint

  given [C1, C2, C3](using (C1 ==> C3) | (C2 ==> C3)): (And[C1, C2] ==> C3) = Implication()


  final class StrictEqual[V]

  inline given [A, V <: A]: Constraint[A, StrictEqual[V]] with

    override inline def test(value: A): Boolean = value == constValue[V]

    override inline def message: String = "Should strictly equal to " + constValue[ToString[V]]
