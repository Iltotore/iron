package io.github.iltotore.iron

import scala.Console.{CYAN, RESET}
import scala.quoted.*

/**
 * Internal macros.
 * @see [[compileTime]] for public compile-time utilities
 */
package object macros:

  /**
   * A FromExpr[Boolean] that can extract value from partially inlined || and
   * && operations.
   *
   * {{{
   *   inline val x = true
   *   val y: Boolean = ???
   *
   *   x || y //inlined to `true`
   *   y || x //inlined to `true`
   *
   *   inline val a = false
   *   val b: Boolean = ???
   *
   *   a && b //inlined to `false`
   *   b && a //inlined to `false`
   * }}}
   */
  given FromExpr[Boolean] with

    override def unapply(expr: Expr[Boolean])(using Quotes): Option[Boolean] =

      import quotes.reflect.*

      def rec(tree: Term): Option[Boolean] =
        tree match
          case Block(stats, e) => if stats.isEmpty then rec(e) else None
          case Inlined(_, bindings, e) =>
            if bindings.isEmpty then rec(e) else None
          case Typed(e, _) => rec(e)
          case Apply(Select(left, "||"), List(right))
              if left.tpe <:< TypeRepr.of[Boolean] && right.tpe <:< TypeRepr
                .of[Boolean] => // OR
            rec(left) match
              case Some(value) => if value then Some(true) else rec(right)
              case None        => rec(right).filter(x => x)
          case Apply(Select(left, "&&"), List(right))
              if left.tpe <:< TypeRepr.of[Boolean] && right.tpe <:< TypeRepr
                .of[Boolean] => // AND
            rec(left) match
              case Some(value) => if value then rec(right) else Some(false)
              case None        => rec(right).filterNot(x => x)
          case _ =>
            tree.tpe.widenTermRefByName match
              case ConstantType(c) => Some(c.value.asInstanceOf[Boolean])
              case _               => None

      rec(expr.asTerm)

  /**
   * A FromExpr[String] that can extract value from concatenated strings if all
   * arguments are compile-time-extractable strings.
   *
   * {{{
   *   inline val x = "a"
   *   inline val y = "b"
   *   val z = "c"
   *
   *   x + y //"ab"
   *   x + z //None
   *   z + x //None
   * }}}
   */
  given FromExpr[String] with

    def unapply(expr: Expr[String])(using Quotes) =

      import quotes.reflect.*

      def rec(tree: Term): Option[String] = tree match
        case Block(stats, e) => if stats.isEmpty then rec(e) else None
        case Inlined(_, bindings, e) =>
          if bindings.isEmpty then rec(e) else None
        case Typed(e, _) => rec(e)
        case Apply(Select(left, "+"), List(right))
            if left.tpe <:< TypeRepr.of[String] && right.tpe <:< TypeRepr
              .of[String] =>
          rec(left).zip(rec(right)).map(_ + _)
        case _ =>
          tree.tpe.widenTermRefByName match
            case ConstantType(c) => Some(c.value.asInstanceOf[String])
            case _               => None

      rec(expr.asTerm)

  /**
   * Asserts at compile time if the given condition is true.
   *
   * @param input the tested input, used in the error message if the assertion fails.
   * @param cond the tested condition. Should be evaluable at compile time.
   * @param message the message/description of this assertion.
   * @tparam A the input type.
   */
  inline def assertCondition[A](inline input: A, inline cond: Boolean, inline message: String): Unit =
    ${ assertConditionImpl('input, 'cond, 'message) }

  private def assertConditionImpl[A: Type](input: Expr[A], cond: Expr[Boolean], message: Expr[String])(using Quotes): Expr[Unit] =

    import quotes.reflect.*

    val messageValue = message.value.getOrElse("<Unknown message>")
    val condValue = cond.value
      .getOrElse(
        report.errorAndAbort(
          s"""Cannot refine value at compile-time because the predicate cannot be evaluated.
             |This is likely because the condition or the input value isn't fully inlined.
             |
             |To test a constraint at runtime, use the `refined` extension method.
             |
             |${CYAN}Inlined input$RESET: ${input.show}
             |${CYAN}Inlined condition$RESET: ${cond.show}
             |${CYAN}Message$RESET: $messageValue""".stripMargin
        )
      )

    if !condValue then report.error(messageValue)
    '{}

  /**
   * Throw compile-time error indicating that the given value is not constant.
   *
   * @param value the non-constant value, used in the error message.
   * @tparam A the type of `value`.
   */
  inline def nonConstantError[A](inline value: A): Nothing = ${ nonConstantErrorImpl('value) }

  private def nonConstantErrorImpl[A](expr: Expr[A])(using Quotes): Nothing =

    import quotes.reflect.*

    report.errorAndAbort(
      s"""Cannot refine non full inlined input at compile-time.
         |To test a constraint at runtime, use the `refined` extension method.
         |
         |Note: Due to a Scala limitation, already-refined types cannot be tested at compile-time (unless proven by an `Implication`).
         |
         |${CYAN}Inlined input$RESET: ${expr.asTerm.show}""".stripMargin
    )

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

end macros
