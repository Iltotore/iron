
package io.github.iltotore.iron

import scala.quoted._

object compileTime {

  /**
   * Try to check [[value]] at compile time if possible or fallbacks to runtime if allowed.
   *
   * Evaluation rules:
   * - Fully inline constraints with inline input are guaranteed to be evaluated at compile time
   *
   * - Non-fully inline constraints or inputs will be as optimized as possible by the language through the inline feature and
   * will be evaluated at runtime
   * @param value the asserted boolean (internally treated as an expression)
   */
  inline def preAssert(inline value: Boolean): Unit = ${preAssertImpl('value)}

  private def preAssertImpl(expr: Expr[Boolean])(using quotes: Quotes): Expr[Unit] = {

    expr.value match {

      case Some(false) => quotes.reflect.report.error("Compile time assertion failed", expr)
        '{()}

      case None => System.getProperty("iron.fallback", "error") match {

        case "error" => quotes.reflect.report.error("Unable to evaluate assertion at compile time", expr)
          '{()}

        case "warn" => quotes.reflect.report.warning("Unable to evaluate assertion at compile time", expr)
          fallback(expr)

        case "allow" => fallback(expr)

        case unknown => quotes.reflect.report.error(s"Unknown option: $unknown. Use error|warn|allow")
          '{()}
      }

      case _ => '{()}
    }
  }

  private def fallback(expr: Expr[Boolean])(using Quotes): Expr[Unit] = '{
    if(!$expr) throw new IllegalArgumentException("Type assertion failed")
  }
}