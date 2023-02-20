package io.github.iltotore.iron.scalacheck

import scala.compiletime.constValue
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.*
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen.Choose

object numeric:

  inline given gt[A : Numeric : Choose, V1 <: A](using max: Max[A], adj: Adjacent[A]): Arbitrary[A :| Greater[V1]] =
    intervalArbitrary(adj.nextUp(constValue[V1]), max.value)

  inline given gteq[A : Numeric: Choose, V1 <: A](using max: Max[A]): Arbitrary[A :| GreaterEqual[V1]] =
    intervalArbitrary(constValue[V1], max.value)

  inline given lt[A: Numeric: Choose, V1 <: A](using min: Min[A], adj: Adjacent[A]): Arbitrary[A :| Less[V1]] =
    intervalArbitrary(min.value, adj.nextDown(constValue[V1]))

  inline given lteq[A: Numeric: Choose, V1 <: A](using min: Min[A]): Arbitrary[A :| LessEqual[V1]] =
    intervalArbitrary(min.value, constValue[V1])
  
  inline given closed[A : Numeric : Choose, V1 <: A, V2 <: A]: Arbitrary[A :| Interval.Closed[V1, V2]] =
    intervalArbitrary(constValue[V1], constValue[V2])

  inline given openClosed[A : Numeric : Choose, V1 <: A, V2 <: A](using adj: Adjacent[A]): Arbitrary[A :| Interval.OpenClosed[V1, V2]] =
    intervalArbitrary(adj.nextUp(constValue[V1]), constValue[V2])

  inline given closedOpen[A : Numeric : Choose, V1 <: A, V2 <: A](using adj: Adjacent[A]): Arbitrary[A :| Interval.ClosedOpen[V1, V2]] =
    intervalArbitrary(constValue[V1], adj.nextDown(constValue[V2]))

  inline given open[A : Numeric : Choose, V1 <: A, V2 <: A](using adj: Adjacent[A]): Arbitrary[A :| Interval.Open[V1, V2]] =
    intervalArbitrary(adj.nextUp(constValue[V1]), adj.nextDown(constValue[V2]))

  def intervalArbitrary[A : Numeric : Choose, C](min: A, max: A): Arbitrary[A :| C] =
    Arbitrary(Gen.chooseNum(min, max)).asInstanceOf