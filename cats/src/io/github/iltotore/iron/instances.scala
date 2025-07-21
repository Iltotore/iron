package io.github.iltotore.iron

import _root_.cats.kernel.*
import _root_.cats.{Eq, Functor, Order, Show}
import algebra.instances.all.*
import algebra.ring.{AdditiveCommutativeMonoid, AdditiveCommutativeSemigroup, MultiplicativeGroup, MultiplicativeMonoid}
import io.github.iltotore.iron.constraint.numeric.*

import scala.util.NotGiven

/**
 * Represent all Cats' typeclass instances for Iron.
 */

private[iron] trait IronCatsInstances extends RefinedTypeInstancesCats

private trait RefinedTypeInstancesCats extends RefinedTypeInstancesCatsLowPriority:
  given [T](using mirror: RefinedType.Mirror[T], ev: Eq[mirror.IronType]): Eq[T] = ev.asInstanceOf[Eq[T]]

  given [T](using mirror: RefinedType.Mirror[T], ev: Order[mirror.IronType]): Order[T] = ev.asInstanceOf[Order[T]]

  given [T](using mirror: RefinedType.Mirror[T], ev: Show[mirror.IronType]): Show[T] = ev.asInstanceOf[Show[T]]

  given [T](using mirror: RefinedType.Mirror[T], ev: PartialOrder[mirror.IronType]): PartialOrder[T] = ev.asInstanceOf[PartialOrder[T]]


private trait RefinedTypeInstancesCatsLowPriority extends IronInstances:

  given [T](using mirror: RefinedType.Mirror[T], ev: Hash[mirror.IronType]): Hash[T] = ev.asInstanceOf[Hash[T]]


private trait IronInstances extends IronInstancesLowPriority:

  given [F[_]](using functor: Functor[F]): MapLogic[F] with

    override def map[A, B](wrapper: F[A], f: A => B): F[B] = functor.map(wrapper)(f)

  // The `NotGiven` implicit parameter is mandatory to avoid ambiguous implicit error when both Eq[A] and Hash[A]/PartialOrder[A] exist
  given [A, C](using ev: Eq[A], notHashOrOrder: NotGiven[Hash[A] | PartialOrder[A]]): Eq[A :| C] =
    ev.asInstanceOf[Eq[A :| C]]

  given [A, C](using ev: PartialOrder[A], notOrder: NotGiven[Order[A]]): PartialOrder[A :| C] =
    ev.asInstanceOf[PartialOrder[A :| C]]

  given [A, C](using ev: Order[A]): Order[A :| C] = ev.asInstanceOf[Order[A :| C]]

  given [A, C](using ev: Show[A]): Show[A :| C] = ev.asInstanceOf[Show[A :| C]]

  given [A, C, V](using ev: LowerBounded[A], implication: C ==> Greater[V]): LowerBounded[A :| C] =
    ev.asInstanceOf[LowerBounded[A :| C]]

  given [A, C, V](using ev: UpperBounded[A], implication: C ==> Greater[V]): UpperBounded[A :| C] =
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

  private def commutativeMonoid[A, C](using inner: CommutativeMonoid[A], bounds: Bounds[A, C]): CommutativeMonoid[A :| C] =
    new CommutativeMonoid[A :| C]:

      override def empty: A :| C = inner.empty.assume[C]

      override def combine(a: A :| C, b: A :| C): A :| C = bounds.shift(inner.combine(a, b))

  given posIntCommutativeMonoid: CommutativeMonoid[Int :| Positive0] = commutativeMonoid[Int, Positive0]

  given posLongCommutativeMonoid: CommutativeMonoid[Long :| Positive0] = commutativeMonoid[Long, Positive0]

  given posFloatCommutativeMonoid: CommutativeMonoid[Float :| Positive0] = commutativeMonoid[Float, Positive0]

  given posDoubleCommutativeMonoid: CommutativeMonoid[Double :| Positive0] = commutativeMonoid[Double, Positive0]

  given negIntCommutativeMonoid: CommutativeMonoid[Int :| Negative0] = commutativeMonoid[Int, Negative0]

  given negLongCommutativeMonoid: CommutativeMonoid[Long :| Negative0] = commutativeMonoid[Long, Negative0]

  given negFloatCommutativeMonoid: CommutativeMonoid[Float :| Negative0] = commutativeMonoid[Float, Negative0]

  given negDoubleCommutativeMonoid: CommutativeMonoid[Double :| Negative0] = commutativeMonoid[Double, Negative0]

/**
 * Cats' instances for Iron that need to have a lower priority to avoid ambiguous implicits.
 */
private trait IronInstancesLowPriority:
  given [A, C](using ev: Hash[A]): Hash[A :| C] = ev.asInstanceOf[Hash[A :| C]]

  private def additiveCommutativeSemigroup[A, C](using
                                                 inner: AdditiveCommutativeSemigroup[A],
                                                 bounds: Bounds[A, C]
                                                ): AdditiveCommutativeSemigroup[A :| C] = (x, y) =>
    bounds.shift(inner.plus(x, y))

  given posIntAdditiveCommutativeSemigroup: AdditiveCommutativeSemigroup[Int :| Positive] = additiveCommutativeSemigroup[Int, Positive]

  given posLongAdditiveCommutativeSemigroup: AdditiveCommutativeSemigroup[Long :| Positive] = additiveCommutativeSemigroup[Long, Positive]

  given posFloatAdditiveCommutativeSemigroup: AdditiveCommutativeSemigroup[Float :| Positive] = additiveCommutativeSemigroup[Float, Positive]

  given posDoubleAdditiveCommutativeSemigroup: AdditiveCommutativeSemigroup[Double :| Positive] = additiveCommutativeSemigroup[Double, Positive]

  given negIntAdditiveCommutativeSemigroup: AdditiveCommutativeSemigroup[Int :| Negative] = additiveCommutativeSemigroup[Int, Negative]

  given negLongAdditiveCommutativeSemigroup: AdditiveCommutativeSemigroup[Long :| Negative] = additiveCommutativeSemigroup[Long, Negative]

  given negFloatAdditiveCommutativeSemigroup: AdditiveCommutativeSemigroup[Float :| Negative] = additiveCommutativeSemigroup[Float, Negative]

  given negDoubleAdditiveCommutativeSemigroup: AdditiveCommutativeSemigroup[Double :| Negative] = additiveCommutativeSemigroup[Double, Negative]

  private def additiveCommutativeMonoid[A, C](using inner: AdditiveCommutativeMonoid[A], bounds: Bounds[A, C]): AdditiveCommutativeMonoid[A :| C] =
    new:

      override def zero: A :| C = inner.zero.assume[C]

      override def plus(x: A :| C, y: A :| C): A :| C = bounds.shift(inner.plus(x, y))

  given posIntAdditiveCommutativeMonoid: AdditiveCommutativeMonoid[Int :| Positive0] = additiveCommutativeMonoid[Int, Positive0]

  given posLongAdditiveCommutativeMonoid: AdditiveCommutativeMonoid[Long :| Positive0] = additiveCommutativeMonoid[Long, Positive0]

  given posFloatAdditiveCommutativeMonoid: AdditiveCommutativeMonoid[Float :| Positive0] = additiveCommutativeMonoid[Float, Positive0]

  given posDoubleAdditiveCommutativeMonoid: AdditiveCommutativeMonoid[Double :| Positive0] = additiveCommutativeMonoid[Double, Positive0]

  given negIntAdditiveCommutativeMonoid: AdditiveCommutativeMonoid[Int :| Negative0] = additiveCommutativeMonoid[Int, Negative0]

  given negLongAdditiveCommutativeMonoid: AdditiveCommutativeMonoid[Long :| Negative0] = additiveCommutativeMonoid[Long, Negative0]

  given negFloatAdditiveCommutativeMonoid: AdditiveCommutativeMonoid[Float :| Negative0] = additiveCommutativeMonoid[Float, Negative0]

  given negDoubleAdditiveCommutativeMonoid: AdditiveCommutativeMonoid[Double :| Negative0] = additiveCommutativeMonoid[Double, Negative0]

  given multiplicativeMonoid[A, C](using inner: MultiplicativeMonoid[A]): MultiplicativeMonoid[A :| C] =
    inner.assumeAll[C]

  given multiplicativeGroup[A, C](using inner: MultiplicativeGroup[A]): MultiplicativeGroup[A :| C] =
    inner.assumeAll[C]


