package io.github.iltotore.iron

import io.github.iltotore.iron.constraint.Consequence
import io.github.iltotore.iron.constraint.Consequence.VerifiedConsequence
import io.github.iltotore.iron.{Constrained, compileTime}

import scala.annotation.targetName
import scala.compiletime.{constValue, summonInline}
import scala.language.implicitConversions
import scala.math.Ordering.Implicits.infixOrderingOps
import scala.util.NotGiven

package object constraint extends LowPriorityConsequence {

  /**
   * Implicit assertion check
   *
   * @param value      the value passed to the assertion
   * @param constraint the applied type constraint
   * @tparam A the input type
   * @tparam B the constraint's dummy
   * @return the value as Constrained (meaning "asserted value")
   * @note Due to a type inference bug of Scala 3, [[constrainedToValue]] was moved to the package object.
   */
  implicit inline def refineValue[A, B, C <: Constraint[A, B]](value: A)(using inline constraint: C): A / B = {
    Constrained(compileTime.preAssert[A, B, C](value, constraint.getMessage(value), constraint.assert(value)))
  }

  /**
   * Represent a part of an algebraic expression.
   *
   * @tparam T the algebra type to avoid clashes
   * @tparam V the value parameter of this algebraic expression
   */
  trait AlgebraPart[T, V]

  /**
   * Represent an entry point of the algebraic expression. Example: `?? < 1d` is an entry point.
   *
   * @tparam T the algebra type to avoid clashes
   */
  trait AlgebraEntryPoint[T]

  /**
   * Alias for binary algebraic operator.
   *
   * @tparam A       the algebra part to the left of the operator
   * @tparam B       the right input to the right of the operator
   * @tparam Alg     the algebra type of this operator
   * @tparam Literal the type of the possible literals for this operator
   * @tparam Left    the constraint type of this operator for the pattern `?? < B` or `A < B`
   * @tparam Right   the constraint type of this operator for the pattern `A > ??`
   */
  type BiOperator[A, B, Alg, Literal, Left[_], Right[_]] = A match {
    case ?? => Left[B]
    case AlgebraEntryPoint[Alg] => AlgebraPartAnd[A, Left[B], Alg, B]
    case AlgebraPart[Alg, v] => AlgebraPartAnd[A, Placehold[Left[B], Alg, v], Alg, v]
    case Literal => B match {
      case ?? => Right[A]
      case _ => A / Left[B]
    }
    case _ => A / Left[B]
  }


  /**
   * A reflexive binary relation `aRb`
   * @tparam V the value of `b`
   */
  trait Reflexive[V]

  transparent inline given [A, V <: A, B[_] <: Reflexive[_]]: Consequence[A, StrictEqual[V], B[V]] = Consequence.verified

  /**
   * A symmetric binary relation `aRb`
   * @tparam V the value of `b`
   * @tparam Sym the opposite relation (`bRa` <=> `a Sym b`)
   */
  trait Symmetric[V, Sym[_]]

  transparent inline given [A, V <: A, Sym[_], B[_] <: Symmetric[_, Sym]]: Consequence[A, B[V], Sym[V]] = Consequence.verified

  /**
   * An antisymmetric binary relation `aRb`
   * @tparam V the value of `b`
   * @tparam Sym the opposite relation (`bRa` <=> `a Sym b`)
   */
  trait Antisymmetric[V, Sym[_]]

  transparent inline given [A, V <: A, Sym[_], B[_] <: Antisymmetric[_, Sym]]: Consequence[A, B[V] && Sym[V], StrictEqual[V]] = Consequence.verified

  /**
   * An asymmetric binary relation `aRb`
   * @tparam V the value of `b`
   * @tparam Sym the opposite relation (`bRa` <=> `a Sym b`)
   */
  trait Asymmetric[V, Sym[_]]

  transparent inline given asym[A, V <: A, Sym[_], B[_] <: Asymmetric[_, Sym]]: Consequence[A, B[V], Sym[V]] = Consequence.invalid

  transparent inline given asymEq[A, V <: A, B <: Asymmetric[V, ?]]: Consequence[A, B, StrictEqual[V]] = Consequence.invalid

  /**
   * A Transitive binary relation `aRb`
   * @tparam V the value of `b`
   */
  trait Transitive[V]

  class TransitiveConsequence[A, V1 <: A, V2 <: A, B[_] <: Transitive[_], C <: Constraint[A, B[V2]]](using C) extends Consequence[A, B[V1], B[V2]] {

    override inline def assert(value: A): Boolean = summonInline[C].assert(constValue[V1]) || summonInline[C].assert(value)

    override inline def getMessage(value: A): String = summonInline[C].getMessage(constValue[V1])
  }

  transparent inline given trans[A, V1 <: A, V2 <: A, B[_] <: Transitive[_], C <: Constraint[A, B[V2]]](using inline constraint: C): Consequence[A, B[V1], B[V2]] = new TransitiveConsequence

  /**
   * A reflexive, transitive and symmetric binary relation `aRb`.
   * @tparam V the value of `b`
   * @tparam Sym the opposite relation (`bRa` <=> `a Sym b`)
   */
  trait Equivalence[V, Sym[_]] extends Reflexive[V] with Antisymmetric[V, Sym] with Transitive[V]

  /**
   * A reflexive, transitive and antisymmetric binary relation `aRb`
   * @tparam V the value of `b`
   * @tparam Sym the opposite relation (`bRa` <=> `a Sym b`)
   */
  trait Order[V, Sym[_]] extends Reflexive[V] with Antisymmetric[V, Sym] with Transitive[V]


  final class Literal[V]

  type NoConstraint = Literal[true]

  class LiteralConstraint[A, V <: Boolean] extends Constraint[A, Literal[V]] {

    override inline def assert(value: A): Boolean = constValue[V]

    override inline def getMessage(value: A): String = inline if (constValue[V]) "true" else "false"
  }

  inline given[A, V <: Boolean]: LiteralConstraint[A, V] = new LiteralConstraint

  /**
   * Placeholder for algebraic expressions
   */
  final class Placeholder

  type ?? = Placeholder

  class PlaceholderConstraint[A] extends Constraint[A, Placeholder] {

    override inline def assert(value: A): Boolean = true

    override inline def getMessage(value: A): String = "True"
  }

  inline given[A]: PlaceholderConstraint[A] = new PlaceholderConstraint

  /**
   * Constraint: checks if the input value strictly equals to V.
   *
   * @tparam V
   */
  final class StrictEqual[V]

  type ==[A, V] = A / StrictEqual[V]

  class StrictEqualConstraint[A, V <: A] extends Constraint[A, StrictEqual[V]] {

    override inline def assert(value: A): Boolean = value == constValue[V]

    override inline def getMessage(value: A): String = s"$value should strictly equal to ${constValue[V]}"
  }

  inline given[A, V <: A]: StrictEqualConstraint[A, V] = new StrictEqualConstraint


  /**
   * Constraint: checks if the input value equals (using Any#equals) to V.
   *
   * @tparam V
   * @note This constraint is runtime-only
   */
  trait Equal[V]

  class EqualConstraint[A, V <: A] extends Constraint[A, Equal[V]] {

    override inline def assert(value: A): Boolean = value equals constValue[V]

    override inline def getMessage(value: A): String = s"$value should equal to ${constValue[V]}"
  }

  inline given[A, V <: A]: EqualConstraint[A, V] = new EqualConstraint


  /**
   * Constraint: checks if the input value doesn't pass B's constraint.
   *
   * @tparam B the reversed constraint's dummy
   */
  trait Not[B]

  type \[A, V] = A / Not[StrictEqual[V]]

  /**
   * Equivalent of the mathematical implication `=>`. Alias for `Not[A] || B`
   *
   * @tparam A the antecedent of B
   * @tparam B the consequence of A
   */
  type ==>>[A, B] = Not[A] || B

  class NotConstraint[A, B, C <: Constraint[A, B]](using C) extends Constraint[A, Not[B]] {

    override inline def assert(value: A): Boolean = !summonInline[C].assert(value)

    override inline def getMessage(value: A): String = summonInline[C].getMessage(value)
  }

  inline given[A, B, C <: Constraint[A, B]](using inline constraint: C): NotConstraint[A, B, C] = new NotConstraint

  /**
   * (B => !B) <=> false
   */
  transparent inline given [A, B]: Consequence[A, B, Not[B]] = Consequence.invalid

  /**
   * (!B => B) <=> false
   */
  transparent inline given [A, B]: Consequence[A, Not[B], B] = Consequence.invalid

  /**
   * Constraint: checks if the value pass B or C. Acts like a boolean OR.
   *
   * @tparam B the first constraint's dummy
   * @tparam C the second constraint's dummy
   */
  trait Or[B, C]

  type ||[B, C] = Or[B, C]

  class OrConstraint[A, B, C, CB <: Constraint[A, B], CC <: Constraint[A, C]](using CB, CC) extends Constraint[A, Or[B, C]] {

    override inline def assert(value: A): Boolean = summonInline[CB].assert(value) || summonInline[CC].assert(value)

    override inline def getMessage(value: A): String = s"${summonInline[CB].getMessage(value)} || ${summonInline[CC].getMessage(value)}"
  }

  inline given[A, B, C, CB <: Constraint[A, B], CC <: Constraint[A, C]](using inline cb: CB, inline cc: CC): OrConstraint[A, B, C, CB, CC] = new OrConstraint

  /**
   * B1 => B1 | B2
   */
  transparent inline given[A, B1, B2]: Consequence[A, B1, Or[B1, B2]] = Consequence.verified

  /**
   * B2 => B1 | B2
   */
  transparent inline given[A, B1, B2]: Consequence[A, B2, Or[B1, B2]] = Consequence.verified


  /**
   * Constraint: checks if the value pass both B and C. Acts like a boolean AND.
   *
   * @tparam B the first constraint's dummy
   * @tparam C the second constraint's dummy
   */
  trait And[B, C]

  type &&[B, C] = And[B, C]

  class AndConstraint[A, B, C, CB <: Constraint[A, B], CC <: Constraint[A, C]](using CB, CC) extends Constraint[A, And[B, C]] {

    override inline def assert(value: A): Boolean = summonInline[CB].assert(value) && summonInline[CC].assert(value)

    override inline def getMessage(value: A): String = s"${summonInline[CB].getMessage(value)} and ${summonInline[CC].getMessage(value)}"
  }

  inline given[A, B, C, CB <: Constraint[A, B], CC <: Constraint[A, C]](using inline cb: CB, inline cc: CC): AndConstraint[A, B, C, CB, CC] = new AndConstraint

  /**
   * B1 & B2 => B1
   */
  transparent inline given [A, B1, B2]: Consequence[A, And[B1, B2], B1] = Consequence.verified

  /**
   * B1 & B2 => B2
   */
  transparent inline given [A, B1, B2]: Consequence[A, And[B1, B2], B2] = Consequence.verified


  final class AlgebraPartAnd[B, C, Alg, V] extends AlgebraPart[Alg, V]

  class AlgebraicConstraint[A, B, C, CB <: Constraint[A, B], CC <: Constraint[A, C], Alg, V](using CB, CC) extends Constraint[A, AlgebraPartAnd[B, C, Alg, V]] {

    override inline def assert(value: A): Boolean = summonInline[CB].assert(value) && summonInline[CC].assert(value)

    override inline def getMessage(value: A): String = s"${summonInline[CB].getMessage(value)} and ${summonInline[CC].getMessage(value)}"
  }

  inline given[A, B, C, CB <: Constraint[A, B], CC <: Constraint[A, C], Alg, V](using inline cb: CB, inline cc: CC): AlgebraicConstraint[A, B, C, CB, CC, Alg, V] =
    new AlgebraicConstraint


  /**
   * Constraint: attaches a custom description to the given constraint. Useful when a constraint alias need a more
   * accurate description.
   *
   * @tparam B the wrapped constraint's dummy
   * @tparam V the description to attach. Must be litteral.
   */
  trait DescribedAs[+B, V]

  class DescribedAsConstraint[A, B, C <: Constraint[A, B], V <: String](using C) extends Constraint[A, DescribedAs[B, V]] {

    override inline def assert(value: A): Boolean = summonInline[C].assert(value)

    override inline def getMessage(value: A): String = constValue[V]
  }

  inline given[A, B, C <: Constraint[A, B], V <: String](using inline constraint: C): DescribedAsConstraint[A, B, C, V] = new DescribedAsConstraint


  /**
   * Constraint: makes the wrapped constraint runtime-only.
   *
   * @tparam B the wrapped constraint's dummy
   */
  trait RuntimeOnly[B]

  class RuntimeOnlyConstraint[A, B, C <: Constraint[A, B]](using constraint: C) extends Constraint.RuntimeOnly[A, RuntimeOnly[B]] {

    override inline def assert(value: A): Boolean = constraint.assert(value)

    override inline def getMessage(value: A): String = constraint.getMessage(value)
  }

  inline given[A, B, C <: Constraint[A, B]](using C): RuntimeOnlyConstraint[A, B, C] = new RuntimeOnlyConstraint


  trait CompileTimeOnly[B]

  class CompileTimeConstraint[A, B, C <: Constraint[A, B]](using C) extends Constraint.CompileTimeOnly[A, CompileTimeOnly[B]] {

    override inline def assert(value: A): Boolean = summonInline[C].assert(value)

    override inline def getMessage(value: A): String = summonInline[C].getMessage(value)
  }

  inline given[A, B, C <: Constraint[A, B]](using inline constraint: C): CompileTimeConstraint[A, B, C] = new CompileTimeConstraint

  /**
   * Represent a constraint with a placehold value as input (instead of the Constrained value). Used for chained algebra.
   *
   * @tparam B   the wrapped constraint
   * @tparam Alg the algebra type of this placehold
   * @tparam V   the value to pass as input
   */
  trait Placehold[B, Alg, V] extends AlgebraPart[Alg, V]

  class PlaceholdConstraint[A, B, C <: Constraint[A, B], Alg, V <: A](using C) extends Constraint[A, Placehold[B, Alg, V]] {

    override inline def assert(value: A): Boolean = summonInline[C].assert(constValue[V])

    override inline def getMessage(value: A): String = summonInline[C].getMessage(constValue[V])
  }

  inline given[A, B, C <: Constraint[A, B], Alg, V <: A](using inline constraint: C): PlaceholdConstraint[A, B, C, Alg, V] = new PlaceholdConstraint
}