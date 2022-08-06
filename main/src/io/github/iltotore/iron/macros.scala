package io.github.iltotore.iron

import scala.quoted.*

object macros {

  transparent inline def assertCondition(inline cond: Boolean, inline message: String): Unit = ${assertConditionImpl('cond, 'message)}

  private def assertConditionImpl(cond: Expr[Boolean], message: Expr[String])(using Quotes): Expr[Unit] =

    val report = quotes.reflect.report

    val condValue = cond.valueOrAbort
    val messageValue = message.value.getOrElse("<Unknown message>")

    if !condValue then report.errorAndAbort(messageValue)
    else '{()}
}
