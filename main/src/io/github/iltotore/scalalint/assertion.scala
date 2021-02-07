package io.github.iltotore.scalalint

import scala.quoted._
import scala.language.implicitConversions
import io.github.iltotore.scalalint.constraint.{CompileTimeConstraint, ConstraintAnchor}
import io.github.iltotore.scalalint.util._

object assertion {
  
  /*inline def compileTime[T : FromExpr](inline expr: T)(using to: ToExpr[Option[T]]): Option[T] = ${compileTimeImpl('expr)}

  def compileTimeImpl[T : FromExpr](expr: Expr[T])(using Quotes): Expr[Option[T]] = {
    val fromOption = summon[FromExpr[T]]
    val toOption = summon[ToExpr[Option[T]]]
    (fromOption, toOption) match {

      case (Some(from), Some(to)) => to.apply(from.unapply(expr))

      case _ => '{None}
    }
  }*/
  
}
