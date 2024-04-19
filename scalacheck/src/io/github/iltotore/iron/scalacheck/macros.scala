package io.github.iltotore.iron.scalacheck

import io.github.iltotore.iron.:|

import org.scalacheck.*
import scala.quoted.*

private transparent inline def unionGen[A, C]: Gen[A :| C] = ${ unionGenImpl[A, C] }

private def unionGenImpl[A, C](using Quotes, Type[A], Type[C]): Expr[Gen[A :| C]] =

  import quotes.reflect.*

  def rec(tpe: TypeRepr): Expr[Gen[?]] =
    tpe.dealias match
      case OrType(left, right) => '{ Gen.oneOf(${ rec(left) }, ${ rec(right) }) }
      case constraintTpe =>
        type ConstraintPart

        given Type[ConstraintPart] = constraintTpe.asType.asInstanceOf

        '{ scala.compiletime.summonInline[Arbitrary[A :| ConstraintPart]].arbitrary }

  '{ ${ rec(TypeRepr.of[C]) }.asInstanceOf[Gen[A :| C]] }.asExprOf[Gen[A :| C]]
