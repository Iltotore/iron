package io.github.iltotore

import scala.annotation.implicitNotFound
import scala.Console.{CYAN, RESET}
import scala.compiletime.{codeOf, error}
import scala.language.implicitConversions
import scala.util.NotGiven

/**
 * The main package of Iron. Contains:
 * - IronType
 * - Refining methods (both compile-time and runtime)
 * - Export of [[constraint.any]].*
 */
package object iron:

  export io.github.iltotore.iron.constraint.any.{*, given}

  /**
   * Union of all numerical primitives.
   * This abstraction facilitates the creation of numerical constraints.
   */
  type Number = Byte | Short | Int | Long | Float | Double

  /**
   * Union of all integer primitives. This abstraction facilitates the creation of numerical constraints.
   */
  type IntNumber = Byte | Short | Int | Long

  /**
   * An Iron type (refined).
   *
   * @tparam A the underlying type.
   * @tparam C the predicate/constraint guarding this type.
   */
  opaque type IronType[A, C] <: A = A

  /**
   * Alias for [[IronType]]. Similar to the mathematical symbol `|` in e.g `{x in R | x > 0}`.
   */
  type :|[A, C] = IronType[A, C]

  object IronType:

    /**
     * Create an IronType.
     *
     * @param value the value to be constrained.
     * @tparam A the refined type.
     * @tparam C the constraint applied to the type.
     * @return the given value typed as [[IronType]].
     * @note this does not check if the constraint is satisfied. Use [[package.refine]] to refine a value at runtime.
     * @see [[package.autoRefine]], [[package.refine]], [[package.refineEither]], [[package.refineOption]]
     */
    inline def apply[A, C](value: A): IronType[A, C] = value

  end IronType

  /**
   * Implicitly refine at compile-time the given value.
   *
   * @param value the value to refine.
   * @param constraint the implementation of `C` to check.
   * @tparam A the refined type.
   * @tparam C the constraint applied to the type.
   * @return the given value typed as [[IronType]]
   *
   * @note This method ensures that the value satisfies the constraint. If it doesn't or isn't evaluable at compile-time, the compilation is aborted.
   */
  implicit inline def autoRefine[A, C](inline value: A)(using
      inline constraint: Constraint[A, C]
  ): A :| C =
    inline if !macros.isConstant(value) then macros.nonConstantError(value)
    macros.assertCondition(value, constraint.test(value), constraint.message)
    value

  /**
   * An algebraic implication between two constraints (e.g transitivity for [[package.constraint.numeric.Greater]]).
   *
   * @tparam C1 the assumed constraint.
   * @tparam C2 the constraint implied by `C1`.
   */
  @implicitNotFound("Could not prove that ${C1} implies ${C2}")
  final class Implication[C1, C2]

  /**
   * Alias for [[Implication]]. Similar to the mathematical implication symbol, often represented by an arrow.
   */
  type ==>[C1, C2] = Implication[C1, C2]

  /**
   * Implicitly cast a constrained value to another if verified.
   *
   * @param value the refined to value to cast.
   * @param Implication the evidence that the original constraint `C1` implies `C2`.
   * @tparam A the refined type.
   * @tparam C1 the original constraint.
   * @tparam C2 the target constraint.
   * @return the given value constrained by `C2`.
   */
  implicit inline def autoCastIron[A, C1, C2](inline value: A :| C1)(using C1 ==> C2): A :| C2 = value

  extension [A](value: A)

    /**
     * Refine the given value at runtime.
     *
     * @param constraint the constraint to test with the value to refine.
     * @return this value as [[IronType]].
     * @throws an [[IllegalArgumentException]] if the constraint is not satisfied.
     * @see [[autoRefine]], [[refineEither]], [[refineOption]].
     */
    inline def refine[B](using inline constraint: Constraint[A, B]): A :| B =
      if constraint.test(value) then value
      else throw IllegalArgumentException(constraint.message)

    /**
     * Refine the given value at runtime, resulting in an [[Either]].
     *
     * @param constraint the constraint to test with the value to refine.
     * @return this value a [[Right]] containing this value [[IronType]] or a [[Left]] containing the constraint message.
     * @see [[autoRefine]], [[refine]], [[refineOption]].
     */
    inline def refineEither[B](using inline constraint: Constraint[A, B]): Either[String, A :| B] =
      Either.cond(constraint.test(value), value, constraint.message)

    /**
     * Refine the given value at runtime, resulting in an [[Option]].
     *
     * @param constraint the constraint to test with the value to refine.
     * @return an Option containing this value as [[IronType]] or [[None]].
     * @see [[autoRefine]], [[refine]], [[refineEither]].
     */
    inline def refineOption[B](using inline constraint: Constraint[A, B]): Option[A :| B] =
      Option.when(constraint.test(value))(value)

end iron
