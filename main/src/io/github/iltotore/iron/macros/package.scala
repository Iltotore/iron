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

  given Printer[Tree] = Printer.TreeAnsiCode

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
           |${MAGENTA}Inlined input$RESET: ${input.asTerm.show}
           |${MAGENTA}Inlined condition$RESET: ${cond.asTerm.show}
           |${MAGENTA}Message$RESET: $messageValue
           |${MAGENTA}Reason$RESET: ${err.prettyPrint()}""".stripMargin
      ),
      identity
    )

  if !condValue then
    compileTimeError(s"""|Could not satisfy a constraint for type $MAGENTA${inputType.show}$RESET.
                         |
                         |${MAGENTA}Value$RESET: ${input.asTerm.show}
                         |${MAGENTA}Message$RESET: $messageValue""".stripMargin)
  '{}

def compileTimeError(msg: String)(using Quotes): Nothing =
  quotes.reflect.report.errorAndAbort(
    s"""|-- Constraint Error --------------------------------------------------------
        |$msg
        |----------------------------------------------------------------------------""".stripMargin
  )
