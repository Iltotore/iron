package io.github.iltotore.iron

import _root_.cats.kernel.{CommutativeMonoid, Hash, LowerBounded, PartialOrder, UpperBounded}
import _root_.cats.{Eq, Monoid, Order, Show, Traverse}
import io.github.iltotore.iron.constraint.numeric.*
import scala.util.NotGiven
import _root_.cats.Functor

/**
 * Represent all Cats' typeclass instances for Iron.
 */
private[iron] trait IronCatsInstances extends IronCatsLowPriority, RefinedTypeOpsCats:

  given [F[_]](using functor: Functor[F]): MapLogic[F] with

    override def map[A, B](wrapper: F[A], f: A => B): F[B] = functor.map(wrapper)(f)

  // The `NotGiven` implicit parameter is mandatory to avoid ambiguous implicit error when both Eq[A] and Hash[A]/PartialOrder[A] exist
  inline given [A, C](using inline ev: Eq[A], notHashOrOrder: NotGiven[Hash[A] | PartialOrder[A]]): Eq[A :| C] = ev.asInstanceOf[Eq[A :| C]]

  inline given [A, C](using inline ev: PartialOrder[A], notOrder: NotGiven[Order[A]]): PartialOrder[A :| C] = ev.asInstanceOf[PartialOrder[A :| C]]

  inline given [A, C](using inline ev: Order[A]): Order[A :| C] = ev.asInstanceOf[Order[A :| C]]

  inline given [A, C](using inline ev: Show[A]): Show[A :| C] = ev.asInstanceOf[Show[A :| C]]

  inline given [A, C, V](using inline ev: LowerBounded[A], implication: C ==> Greater[V]): LowerBounded[A :| C] =
    ev.asInstanceOf[LowerBounded[A :| C]]

  inline given [A, C, V](using inline ev: UpperBounded[A], implication: C ==> Greater[V]): UpperBounded[A :| C] =
    ev.asInstanceOf[UpperBounded[A :| C]]

  private def posMonoid[A, C](using ev: CommutativeMonoid[A], shift: PosShift[A], implication: C ==> Positive): CommutativeMonoid[A :| C] =
    new CommutativeMonoid[A :| C]:

      override def empty: A :| C = ev.empty.asInstanceOf[A :| C]

      override def combine(a: A :| C, b: A :| C): A :| C = shift.shift(ev.combine(a, b)).asInstanceOf[A :| C]

  inline given posIntCommutativeMonoid[C](using C ==> Positive): CommutativeMonoid[Int :| C] = posMonoid

  inline given posLongCommutativeMonoid[C](using C ==> Positive): CommutativeMonoid[Long :| C] = posMonoid

  inline given posFloatCommutativeMonoid[C](using C ==> Positive): CommutativeMonoid[Float :| C] = posMonoid

  inline given posDoubleCommutativeMonoid[C](using C ==> Positive): CommutativeMonoid[Double :| C] = posMonoid

  private def negMonoid[A, C](using ev: CommutativeMonoid[A], shift: NegShift[A], implication: C ==> Negative): CommutativeMonoid[A :| C] =
    new CommutativeMonoid[A :| C]:

      override def empty: A :| C = ev.empty.asInstanceOf[A :| C]

      override def combine(a: A :| C, b: A :| C): A :| C = shift.shift(ev.combine(a, b)).asInstanceOf[A :| C]

  inline given negIntCommutativeMonoid[C](using C ==> Negative): CommutativeMonoid[Int :| C] = negMonoid

  inline given negLongCommutativeMonoid[C](using C ==> Negative): CommutativeMonoid[Long :| C] = negMonoid

  inline given negFloatCommutativeMonoid[C](using C ==> Negative): CommutativeMonoid[Float :| C] = negMonoid

  inline given negDoubleCommutativeMonoid[C](using C ==> Negative): CommutativeMonoid[Double :| C] = negMonoid

/**
 * Cats' instances for Iron that need to have a lower priority to avoid ambiguous implicits.
 */
private trait IronCatsLowPriority:

  inline given [A, C](using inline ev: Hash[A]): Hash[A :| C] = ev.asInstanceOf[Hash[A :| C]]

private trait RefinedTypeOpsCats extends RefinedTypeOpsCatsLowPriority:

  inline given [T](using mirror: RefinedTypeOps.Mirror[T], ev: Eq[mirror.IronType]): Eq[T] = ev.asInstanceOf[Eq[T]]

  inline given [T](using mirror: RefinedTypeOps.Mirror[T], ev: Order[mirror.IronType]): Order[T] = ev.asInstanceOf[Order[T]]

  inline given [T](using mirror: RefinedTypeOps.Mirror[T], ev: Show[mirror.IronType]): Show[T] = ev.asInstanceOf[Show[T]]

  inline given [T](using mirror: RefinedTypeOps.Mirror[T], ev: PartialOrder[mirror.IronType]): PartialOrder[T] = ev.asInstanceOf[PartialOrder[T]]

private trait RefinedTypeOpsCatsLowPriority:

  inline given [T](using mirror: RefinedTypeOps.Mirror[T], ev: Hash[mirror.IronType]): Hash[T] = ev.asInstanceOf[Hash[T]]
