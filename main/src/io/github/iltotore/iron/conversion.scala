package io.github.iltotore.iron

import io.github.iltotore.iron.constraint.collection.ForAll

import scala.language.implicitConversions

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
implicit inline def autoRefine[A, C](inline value: A)(using inline constraint: Constraint[A, C]): A :| C =
  inline if !macros.isConstant(value) then macros.nonConstantError(value)
  macros.assertCondition(value, constraint.test(value), constraint.message)
  IronType(value)

/**
 * Implicitly cast a constrained value to another if verified.
 *
 * @param value the refined to value to cast.
 * @param `C1 ==> C2` the evidence that the original constraint `C1` implies `C2`.
 * @tparam A the refined type.
 * @tparam C1 the original constraint.
 * @tparam C2 the target constraint.
 * @return the given value constrained by `C2`.
 */
implicit inline def autoCastIron[A, C1, C2](inline value: A :| C1)(using C1 ==> C2): A :| C2 = value.asInstanceOf

/**
 * Implicitly cast an iterable of elements constrained by `C1` into an iterable constrained by `ForAll[C2]` if `C1` implies `C2`.
 * @param iterable the iterable to factorize.
 * @param `C1 ==> C2` the evidence that `C1` implies `C2`.
 * @tparam A the refined type.
 * @tparam I the iterable type.
 * @tparam C1 the original constraint.
 * @tparam C2 the target constraint.
 * @return the given value as instance of `I[A] :| ForAll[C2]`.
 * @see [[autoDistribute]]
 */
implicit inline def autoFactorize[A, I[_] <: Iterable[_], C1, C2](inline iterable: I[A :| C1])(using C1 ==> C2): I[A] :| ForAll[C2] =
  iterable.asInstanceOf

/**
 * Implicitly cast an iterable constrained by `ForAll[C1]` into an iterable of elements constrained by `C2` if `C1` implies `C2`.
 * @param iterable the iterable to factorize.
 * @param `C1 ==> C2` the evidence that `C1` implies `C2`.
 * @tparam A the refined type.
 * @tparam I the iterable type.
 * @tparam C1 the original constraint.
 * @tparam C2 the target constraint.
 * @return the given value as instance of `I[A :| C2]`.
 * @see [[autoFactorize]]
 */
implicit inline def autoDistribute[A, I[_] <: Iterable[_], C1, C2](inline iterable: I[A] :| ForAll[C1])(using C1 ==> C2): I[A :| C2] =
  iterable.asInstanceOf