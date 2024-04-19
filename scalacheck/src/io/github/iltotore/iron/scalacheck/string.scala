package io.github.iltotore.iron.scalacheck

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.collection.*
import io.github.iltotore.iron.constraint.string.*
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Gen.Choose

import scala.compiletime.constValue

object string:
  inline given notEmptyString: Arbitrary[String :| Not[Empty]] = collection.notEmptyCollection[String, Char]
  inline given startWith[V <: String]: Arbitrary[String :| StartWith[V]] =
    Arbitrary(Gen.asciiStr.map(constValue[V] + _)).asInstanceOf
  inline given endWith[V <: String]: Arbitrary[String :| EndWith[V]] =
    Arbitrary(Gen.asciiStr.map(_ + constValue[V])).asInstanceOf
  inline given minLength[V <: Int](using listArb: Arbitrary[List[Char] :| MinLength[V]]): Arbitrary[String :| MinLength[V]] = reuseCollection
  inline given maxLength[V <: Int](using listArb: Arbitrary[List[Char] :| MaxLength[V]]): Arbitrary[String :| MaxLength[V]] = reuseCollection
  inline given exactLength[V <: Int](using listArb: Arbitrary[List[Char] :| FixedLength[V]]): Arbitrary[String :| FixedLength[V]] = reuseCollection
  inline given emptyLength(using listArb: Arbitrary[List[Char] :| Empty]): Arbitrary[String :| Empty] = reuseCollection
  inline given forAll[V](using listArb: Arbitrary[List[Char] :| ForAll[V]]): Arbitrary[String :| ForAll[V]] = reuseCollection
  inline given init[V](using listArb: Arbitrary[List[Char] :| Init[V]]): Arbitrary[String :| Init[V]] = reuseCollection
  inline given tail[V](using listArb: Arbitrary[List[Char] :| Tail[V]]): Arbitrary[String :| Tail[V]] = reuseCollection
  inline given head[V](using listArb: Arbitrary[List[Char] :| Head[V]]): Arbitrary[String :| Head[V]] = reuseCollection
  inline given last[V](using listArb: Arbitrary[List[Char] :| Last[V]]): Arbitrary[String :| Last[V]] = reuseCollection
  inline given contain[V <: String](using stringArb: Arbitrary[String]): Arbitrary[String :| Contain[V]] =
    Arbitrary(for
      prefix <- stringArb.arbitrary
      suffix <- stringArb.arbitrary
    yield prefix + constValue[V] + suffix).asInstanceOf
  private inline def reuseCollection[C1, C2](using arb: Arbitrary[List[Char] :| C1]): Arbitrary[String :| C2] =
    Arbitrary(arb.arbitrary.map(_.mkString(""))).asInstanceOf
