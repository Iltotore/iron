
package io.github.iltotore.scalalint

import scala.quoted._
import io.github.iltotore.scalalint.constraint.AssertionResult

object compileTime {

  inline def preAssert(inline value: Boolean): Unit = ${preAssertImpl('value)}

  def preAssertImpl(expr: Expr[Boolean])(using quotes: Quotes): Expr[Unit] = {

    expr.value match {

      case Some(false) => quotes.reflect.report.error(s"Compile-time assertion failed", expr)

      case None => quotes.reflect.report.warning(s"Unable to evaluate assertion at compile time", expr)

      case _ =>
    }

    '{()}
  }
}