package io.github.iltotore.iron.macros

import io.github.iltotore.iron.internal.{IronConfig, colorized}
import scala.Console.{MAGENTA, RESET}
import scala.quoted.*
import io.github.iltotore.iron.IronType
import io.github.iltotore.iron.Implication
import io.github.iltotore.iron.internal.IronConfig.CodeFormat

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

  given config: IronConfig = IronConfig.fromSystem
  given Printer[Tree] = config.codeFormat match
    case CodeFormat.Full => Printer.TreeCode
    case CodeFormat.FullColored => Printer.TreeAnsiCode
    case CodeFormat.Short => Printer.TreeShortCode
    case CodeFormat.Structure => Printer.TreeStructure

  val rflUtil = reflectUtil(using quotes)
  import rflUtil.*

  val inputType = TypeRepr.of[A]

  val messageValue = message.decode.getOrElse("<Unknown message>")

  def condError(failure: DecodingFailure): Nothing =
    if config.shortMessages then
      report.errorAndAbort("Cannot refine value at compile-time.")
    else
      val reason =
        if config.shortReasons then
          failure
            .rootCauses
            .groupMapReduce(_.message)(failure => s"  - at ${failure.position}")(_ + "\n" + _)
            .map((msg, pos) => s"$msg:\n" + pos)
            .mkString("\n- ", "\n- ", "")
        else failure.prettyPrint()
      compileTimeError(
        s"""Cannot refine value at compile-time because the predicate cannot be evaluated.
           |This is likely because the condition or the input value isn't fully inlined.
           |
           |To test a constraint at runtime, use one of the `refine...` extension methods.
           |
           |${"Inlined input".colorized(MAGENTA)}: ${input.asTerm.show}
           |${"Inlined condition".colorized(MAGENTA)}: ${cond.asTerm.show}
           |${"Message".colorized(MAGENTA)}: $messageValue
           |${"Reason".colorized(MAGENTA)}: $reason""".stripMargin
      )

  val inputValue = input.decode.toOption
  val condValue = cond.decode.fold(condError, identity)

  if !condValue then
    if config.shortMessages then
      report.errorAndAbort(s"$messageValue: ${inputValue.getOrElse(input.show)}")
    else
      compileTimeError(s"""|Could not satisfy a constraint for type ${inputType.show.colorized(MAGENTA)}.
                           |
                           |${"Value".colorized(MAGENTA)}: ${inputValue.getOrElse(input.show)}
                           |${"Message".colorized(MAGENTA)}: $messageValue""".stripMargin)

  '{}

def compileTimeError(msg: String)(using Quotes): Nothing =
  quotes.reflect.report.errorAndAbort(
    s"""|-- Constraint Error --------------------------------------------------------
        |$msg
        |----------------------------------------------------------------------------""".stripMargin
  )

inline def isIronType[T, C]: Boolean = ${ isIronTypeImpl[T, C] }
def isIronTypeImpl[T: Type, C: Type](using Quotes): Expr[Boolean] =
  import quotes.reflect.*

  val ironType = TypeRepr.of[IronType]
  val implicationType = TypeRepr.of[Implication]
  val targetConstraintType = TypeRepr.of[C]

  TypeRepr.of[T].dealias match
    case AppliedType(tpe, List(baseType, constraintType)) if tpe =:= ironType =>
      Implicits.search(implicationType.appliedTo(List(constraintType, targetConstraintType))) match
        case _: ImplicitSearchSuccess => Expr(true)
        case _: ImplicitSearchFailure => Expr(false)

    case _ => Expr(false)