
package io.github.iltotore.iron

import scala.quoted._

object compileTime {

  inline def preAssert(inline value: Boolean): Boolean = ${preAssertImpl('value)}

  private def preAssertImpl(expr: Expr[Boolean])(using quotes: Quotes): Expr[Boolean] = {

    expr.value match {

      case Some(false) => quotes.reflect.report.error("Compile-time assertion failed", expr)

      case None => System.getProperty("iron.fallback", "error") match {

        case "error" => quotes.reflect.report.error("Unable to evaluate assertion at compile time", expr)

        case "warn" => quotes.reflect.report.warning("Unable to evaluate assertion at compile time", expr)

        case "allow" =>

        case unknown => quotes.reflect.report.error(s"Unknown option: $unknown. Use error|warn|allow")
      }

      case _ =>
    }

    expr
  }
}