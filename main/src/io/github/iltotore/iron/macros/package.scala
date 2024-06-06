package io.github.iltotore.iron.macros

import scala.Console.{MAGENTA, RESET}
import scala.quoted.*

/**
 * Asserts at compile time if the given condition is true.
 *
 * @param input the tested input, used in the error message if the assertion fails.
 * @param cond the tested condition. Should be evaluable at compile time.
 * @param message the message/description of this assertion.
 * @tparam A the input type.
 */
inline def assertCondition[A](inline input: A, inline cond: Boolean, inline message: String): Unit =
  ${ assertConditionImpl[A]('input, 'cond, 'message) }

private def assertConditionImpl[A: Type](input: Expr[A], cond: Expr[Boolean], message: Expr[String])(using Quotes): Expr[Unit] =

  import quotes.reflect.*
  val rflUtil = reflectUtil(using quotes)
  import rflUtil.*

  val inputType = TypeRepr.of[A]

  val messageValue = message.decode.getOrElse("<Unknown message>")
  val condValue = cond.decode
    .fold(
      err => compileTimeError(
        s"""Cannot refine value at compile-time because the predicate cannot be evaluated.
           |This is likely because the condition or the input value isn't fully inlined.
           |
           |To test a constraint at runtime, use one of the `refine...` extension methods.
           |
           |${MAGENTA}Inlined input$RESET: ${input.show}
           |${MAGENTA}Inlined condition$RESET: ${cond.show}
           |${MAGENTA}Message$RESET: $messageValue
           |${MAGENTA}Reason$RESET: $err""".stripMargin
      ),
      identity
    )

  if !condValue then
    compileTimeError(s"""|Could not satisfy a constraint for type $MAGENTA${inputType.show}$RESET.
                         |
                         |${MAGENTA}Value$RESET: ${input.show}
                         |${MAGENTA}Message$RESET: $messageValue""".stripMargin)
  '{}

/**
 * Checks if the given value is constant (aka evaluable at compile time).
 *
 * @param value the value to test.
 * @tparam A the type of `value`.
 * @return `true` if the given value is constant, `false` otherwise.
 */
inline def isConstant[A](inline value: A): Boolean = ${ isConstantImpl('{ value }) }

private def isConstantImpl[A: Type](expr: Expr[A])(using Quotes): Expr[Boolean] =

  import quotes.reflect.*

  val aType = TypeRepr.of[A]

  val result: Boolean =
    if aType <:< TypeRepr.of[Boolean] then expr.asExprOf[Boolean].value.isDefined
    else if aType <:< TypeRepr.of[Byte] then expr.asExprOf[Byte].value.isDefined
    else if aType <:< TypeRepr.of[Short] then expr.asExprOf[Short].value.isDefined
    else if aType <:< TypeRepr.of[Int] then expr.asExprOf[Int].value.isDefined
    else if aType <:< TypeRepr.of[Long] then expr.asExprOf[Long].value.isDefined
    else if aType <:< TypeRepr.of[Float] then expr.asExprOf[Float].value.isDefined
    else if aType <:< TypeRepr.of[Double] then expr.asExprOf[Double].value.isDefined
    else if aType <:< TypeRepr.of[Char] then expr.asExprOf[Char].value.isDefined
    else if aType <:< TypeRepr.of[String] then expr.asExprOf[String].value.isDefined
    else false

  Expr(result)

def compileTimeError(msg: String)(using Quotes): Nothing =
  quotes.reflect.report.errorAndAbort(
    s"""|-- Constraint Error --------------------------------------------------------
        |$msg
        |----------------------------------------------------------------------------""".stripMargin
  )
