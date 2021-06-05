
package io.github.iltotore.iron

import scala.quoted._

object compileTime {

  inline def preAssert(inline value: Boolean): Boolean = ${preAssertImpl('value)}

  def preAssertImpl(expr: Expr[Boolean])(using quotes: Quotes): Expr[Boolean] = {

    expr.value match {

      case Some(false) => quotes.reflect.report.error(s"Compile-time assertion failed", expr)

      case None => quotes.reflect.report.warning(s"Unable to evaluate assertion at compile time", expr)

      case _ =>
    }

    expr
  }
}