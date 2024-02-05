package io.github.iltotore.iron.scalacheck

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.collection.*
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Gen.Choose
import org.scalacheck.util.Buildable

import scala.collection.IterableFactory
import scala.compiletime.constValue

object collection:

  inline given empty[A, CC[_]](using arbElem: Arbitrary[A], evb: Buildable[A, CC[A]], evt: CC[A] => Iterable[A]): Arbitrary[CC[A] :| Empty] = exactLength[A, CC, 0].asInstanceOf

  inline given exactLength[A, CC[_], V <: Int](using arbElem: Arbitrary[A], evb: Buildable[A, CC[A]], evt: CC[A] => Iterable[A]): Arbitrary[CC[A] :| FixedLength[V]] =
    Arbitrary(Gen.infiniteLazyList(arbElem.arbitrary).flatMap(ll => evb.fromIterable(ll.take(constValue[V])))).asInstanceOf
  
  inline given minLength[A, CC[_], V <: Int](using arbElem: Arbitrary[A], evb: Buildable[A,CC[A]], evt: CC[A] => Iterable[A]): Arbitrary[CC[A] :| MinLength[V]] =
    Arbitrary(
      for
        prefix <- Gen.infiniteLazyList(arbElem.arbitrary)
        postfix <- Gen.listOf(arbElem.arbitrary)
      yield
        evb.fromIterable(prefix.take(constValue[V]) ++ postfix)  
    ).asInstanceOf
    
  inline given maxLength[A, CC[_], V <: Int](using arbElem: Arbitrary[A], evb: Buildable[A,CC[A]], evt: CC[A] => Iterable[A]): Arbitrary[CC[A] :| MaxLength[V]] =
    Arbitrary(Gen.containerOf(arbElem.arbitrary).flatMap(cc => Gen.const(evt(cc).take(constValue[V])))).asInstanceOf[Arbitrary[CC[A] :| MaxLength[V]]]

  inline given length[A, CC[_], C](using arbLength: Arbitrary[Int :| C], arbElem: Arbitrary[A], evb: Buildable[A,CC[A]], evt: CC[A] => Iterable[A]): Arbitrary[CC[A] :| Length[C]] =
    Arbitrary(arbLength.arbitrary.flatMap(n => Gen.containerOfN(n, arbElem.arbitrary))).asInstanceOf

  inline given contain[A, V <: A, CC[_]] (using arb: Arbitrary[A], buildable: Buildable[A, CC[A]], evt: CC[A] => Iterable[A]): Arbitrary[CC[A] :| Contain[V]] =
    Arbitrary(
      Gen.containerOf(arb.arbitrary)
        .map(cc => (buildable.builder ++= evt(cc) += constValue[V]).result())
    ).asInstanceOf

  inline given forAll[A, CC[_], C](using arb: Arbitrary[A :| C], evb: Buildable[A :| C, CC[A :| C]], evt: CC[A :| C] => Iterable[A :| C]): Arbitrary[CC[A] :| ForAll[C]] =
    Arbitrary.arbContainer[CC, A :| C].asInstanceOf

  inline given init[A, CC[_], C](using arb: Arbitrary[A], arbConstrained: Arbitrary[A :| C], buildable: Buildable[A, CC[A]], evt: CC[A] => Iterable[A]): Arbitrary[CC[A] :| Init[C]] =
    Arbitrary(
      for
        headValue <- arb.arbitrary
        constrainedValues <- Gen.containerOf[CC, A](arbConstrained.arbitrary)
      yield (buildable.builder ++= evt(constrainedValues) += headValue).result()
    ).asInstanceOf

  inline given tail[A, CC[_], C](using arb: Arbitrary[A], arbConstrained: Arbitrary[A :| C], buildable: Buildable[A, CC[A]], evt: CC[A] => Iterable[A]): Arbitrary[CC[A] :| Tail[C]] =
    Arbitrary(
      for
        headValue <- arb.arbitrary
        constrainedValues <- Gen.containerOf[CC, A](arbConstrained.arbitrary)
      yield (buildable.builder += headValue ++= evt(constrainedValues) ).result()
    ).asInstanceOf

  inline given exists[A, CC[_], C](using arb: Arbitrary[A], arbConstrained: Arbitrary[A :| C], buildable: Buildable[A, CC[A]], evt: CC[A] => Iterable[A]): Arbitrary[CC[A] :| Exists[C]] =
    Arbitrary(
      for
        constrainedValue <- arbConstrained.arbitrary
        init <- Gen.containerOf[CC, A](arb.arbitrary)
        tail <- Gen.containerOf[CC, A](arb.arbitrary)
      yield (buildable.builder ++= evt(init) += constrainedValue ++= evt(tail)).result()
    ).asInstanceOf

  inline given head[A, CC[_], C](using arb: Arbitrary[A], arbConstrained: Arbitrary[A :| C], buildable: Buildable[A, CC[A]], evt: CC[A] => Iterable[A]): Arbitrary[CC[A] :| Head[C]] =
    Arbitrary(
      for
        constrainedHead <- arbConstrained.arbitrary
        tail <- Gen.containerOf[CC, A](arb.arbitrary)
      yield (buildable.builder += constrainedHead ++= evt(tail)).result()
    ).asInstanceOf

  inline given last[A, CC[_], C](using arb: Arbitrary[A], arbConstrained: Arbitrary[A :| C], buildable: Buildable[A, CC[A]], evt: CC[A] => Iterable[A]): Arbitrary[CC[A] :| Last[C]] =
    Arbitrary(
      for
        constrainedLast <- arbConstrained.arbitrary
        init <- Gen.containerOf[CC, A](arb.arbitrary)
      yield (buildable.builder ++= evt(init) += constrainedLast ).result()
    ).asInstanceOf

  inline given notEmptyCollection[CC, A](using evb: Buildable[A, CC], ev2: CC => Iterable[A], arb: Arbitrary[A]): Arbitrary[CC :| Not[Empty]] = Arbitrary(Gen.nonEmptyBuildableOf[CC, A](arb.arbitrary)).asInstanceOf
