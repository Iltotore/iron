package io.github.iltotore.scalalint

import scala.quoted._

object compileTime {

  inline def preAssert(inline value: Option[(Boolean, String)]): Option[String] = ${preAssertImpl('value)}

  def preAssertImpl(expr: Expr[Option[(Boolean, String)]])(using quotes: Quotes): Expr[Option[String]] = {
    
    expr.value.flatten.foreach {
        
      case (true, msg) => quotes.reflect.report.error(s"Constraint failed: $msg", expr)

      case (false, msg) => quotes.reflect.report.warning(s"Constraint failed: $msg", expr)
    }
    
    '{
      $expr.map(_._2)
    }
  }
}