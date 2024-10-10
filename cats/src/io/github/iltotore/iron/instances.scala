package io.github.iltotore.iron

import _root_.cats.kernel.{CommutativeSemigroup, Hash, LowerBounded, PartialOrder, UpperBounded}
import _root_.cats.{Eq, Monoid, Order, Show, Traverse}
import io.github.iltotore.iron.constraint.numeric.*
import scala.util.NotGiven
import _root_.cats.Functor
import algebra.instances.all.*
import algebra.ring.AdditiveCommutativeSemigroup

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

  private def commutativeSemigroup[A, C](using inner: CommutativeSemigroup[A], bounds: Bounds[A, C]): CommutativeSemigroup[A :| C] =
    new CommutativeSemigroup[A :| C]:

      override def combine(a: A :| C, b: A :| C): A :| C = bounds.shift(inner.combine(a, b))

  given posIntCommutativeSemigroup: CommutativeSemigroup[Int :| Positive] = commutativeSemigroup[Int, Positive]
  given posLongCommutativeSemigroup: CommutativeSemigroup[Long :| Positive] = commutativeSemigroup[Long, Positive]
  given posFloatCommutativeSemigroup: CommutativeSemigroup[Float :| Positive] = commutativeSemigroup[Float, Positive]
  given posDoubleCommutativeSemigroup: CommutativeSemigroup[Double :| Positive] = commutativeSemigroup[Double, Positive]

  given negIntCommutativeSemigroup: CommutativeSemigroup[Int :| Negative] = commutativeSemigroup[Int, Negative]
  given negLongCommutativeSemigroup: CommutativeSemigroup[Long :| Negative] = commutativeSemigroup[Long, Negative]
  given negFloatCommutativeSemigroup: CommutativeSemigroup[Float :| Negative] = commutativeSemigroup[Float, Negative]
  given negDoubleCommutativeSemigroup: CommutativeSemigroup[Double :| Negative] = commutativeSemigroup[Double, Negative]

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

  private def additiveCommutativeSemigroup[A, C](using inner: AdditiveCommutativeSemigroup[A], bounds: Bounds[A, C]): AdditiveCommutativeSemigroup[A :| C] = (x, y) =>
    bounds.shift(inner.plus(x, y))

  given posIntAdditiveCommutativeSemigroup: AdditiveCommutativeSemigroup[Int :| Positive] = additiveCommutativeSemigroup[Int, Positive]
  given posLongAdditiveCommutativeSemigroup: AdditiveCommutativeSemigroup[Long :| Positive] = additiveCommutativeSemigroup[Long, Positive]
  given posFloatAdditiveCommutativeSemigroup: AdditiveCommutativeSemigroup[Float :| Positive] = additiveCommutativeSemigroup[Float, Positive]
  given posDoubleAdditiveCommutativeSemigroup: AdditiveCommutativeSemigroup[Double :| Positive] = additiveCommutativeSemigroup[Double, Positive]

  given negIntAdditiveCommutativeSemigroup: AdditiveCommutativeSemigroup[Int :| Negative] = additiveCommutativeSemigroup[Int, Negative]
  given negLongAdditiveCommutativeSemigroup: AdditiveCommutativeSemigroup[Long :| Negative] = additiveCommutativeSemigroup[Long, Negative]
  given negFloatAdditiveCommutativeSemigroup: AdditiveCommutativeSemigroup[Float :| Negative] = additiveCommutativeSemigroup[Float, Negative]
  given negDoubleAdditiveCommutativeSemigroup: AdditiveCommutativeSemigroup[Double :| Negative] = additiveCommutativeSemigroup[Double, Negative]

  