package io.github.iltotore.scalalint

import scala.quoted._

object compileTime {

  /*inline def preEvaluate[T](inline expr: T): Option[T] = ${preEvaluateImpl('expr)}

  def preEvaluateImpl[T : Type](expr: Expr[T])(using Quotes): Expr[Option[T]] = {
    Expr(expr.value)
  }*/

  inline def preAssert(inline value: Option[String]): Option[String] = ${preAssertImpl('value)}

  def preAssertImpl(expr: Expr[Option[String]])(using quotes: Quotes): Expr[Option[String]] = {
    expr.value.flatten.foreach((str: String) => quotes.reflect.report.error(s"Constraint failed: $str", expr))
    expr
  }
}