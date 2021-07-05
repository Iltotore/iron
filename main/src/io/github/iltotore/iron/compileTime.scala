
package io.github.iltotore.iron

import constraint.{Constraint, IllegalValueError}
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
   *
   * @param value the asserted boolean (internally treated as an expression)
   */
  inline def preAssert[A, B, C <: Constraint[A, B]](inline input: A, inline constraint: C): Refined[A] = $
  {preAssertImpl('input, 'constraint, '
  {constraint.assert(input)})}

  private def preAssertImpl[A: Type, B: Type, C <: Constraint[A, B]: Type](input: Expr[A], constraint: Expr[C], result: Expr[Boolean])(using quotes: Quotes): Expr[Refined[A]] = {

    import quotes.reflect.*

    result.value match {

      case Some(false) if !(TypeRepr.of[C] <:< TypeRepr.of[Constraint.RuntimeOnly[?, ?]]) =>
        report.error("Compile time assertion failed", result)

      case None => System.getProperty("iron.fallback", "allow") match {

        case _ if TypeRepr.of[C] <:< TypeRepr.of[Constraint.CompileTimeOnly[?, ?]] =>
          report.error("Unable to evaluate CompileTimeOnly assertion", result)

        case "error" => report.error("Unable to evaluate assertion at compile time", result)

        case "warn" => report.warning("Unable to evaluate assertion at compile time", result)

        case "allow" =>

        case unknown => report.error(s"Unknown option: $unknown. Use error|warn|allow")
      }

      case _ =>
    }

    '{Either.cond($result, $input, IllegalValueError($input, $constraint))}
  }
}