package io.github.iltotore.iron.scalacheck

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.string.*
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Gen.Choose

import scala.compiletime.constValue

object string:

  inline given startWith[V <: String]: Arbitrary[String :| StartWith[V]] =
    Arbitrary(Gen.asciiStr.map(constValue[V] + _)).asInstanceOf

  inline given endWith[V <: String]: Arbitrary[String :| EndWith[V]] =
    Arbitrary(Gen.asciiStr.map(_ + constValue[V])).asInstanceOf