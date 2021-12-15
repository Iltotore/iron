
package io.github.iltotore.iron

import constraint.{Constraint, IllegalValueError, Not}

import scala.quoted.*

object compileTime {

  /**
   * Try to check the passed constraint with the given input at compile time if possible or fallbacks to runtime if allowed.
   *
   * Evaluation rules:
   * - Fully inline constraints with inline input are guaranteed to be evaluated at compile time
   *
   * - Non-fully inline constraints or inputs will be as optimized as possible by the language through the inline feature and
   * will be evaluated at runtime
   *
   * @param input the constrained input
   * @param message the error message to throw if the result is false
   * @param result the result of the assertion
   */
  inline def preAssert[A, B, C <: Constraint[A, B]](inline input: A, inline message: String, inline result: Boolean): Refined[A] = ${preAssertImpl[A, B, C]('input, 'message, 'result)}

  private def preAssertImpl[A: Type, B: Type, C <: Constraint[A, B]: Type](input: Expr[A], message: Expr[String], result: Expr[Boolean])(using quotes: Quotes): Expr[Refined[A]] = {

    import quotes.reflect.*

    val msg = message.value.getOrElse("<Unknown>")

    result.value match {

      case Some(false) if !(TypeRepr.of[C] <:< TypeRepr.of[Constraint.RuntimeOnly[?, ?]]) =>
        report.error(s"Compile time assertion failed: $msg", result)

      case None => System.getProperty("iron.fallback", "allow") match {

        case _ if TypeRepr.of[C] <:< TypeRepr.of[Constraint.CompileTimeOnly[?, ?]] =>
          report.error(s"Unable to evaluate CompileTimeOnly assertion: $msg", result)

        case "error" => report.error(s"Unable to evaluate assertion at compile tim: $msg", result)

        case "warn" => report.warning(s"Unable to evaluate assertion at compile time: $msg", result)

        case "allow" =>

        case unknown => report.error(s"Unknown option: $unknown. Use error|warn|allow")
      }

      case _ =>
    }

    '{Either.cond($result, $input, IllegalValueError($input, $message))}
  }
}