package io.github.iltotore.iron

import scala.Console.{CYAN, RESET}
import scala.annotation.tailrec
import scala.quoted.*

object macros:

  /** A FromExpr[Boolean] that can extract value from partially inlined || and
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
    *   x && y //inlined to `false`
    *   y || x //inlined to `false`
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
          case Apply(Select(left, "||"), List(right)) if left.tpe <:< TypeRepr.of[Boolean] && right.tpe <:< TypeRepr.of[Boolean] => // OR
            rec(left) match
              case Some(value) => if value then Some(true) else rec(right)
              case None        => rec(right).filter(x => x)
          case Apply(Select(left, "&&"), List(right)) if left.tpe <:< TypeRepr.of[Boolean] && right.tpe <:< TypeRepr.of[Boolean] => // AND
            rec(left) match
              case Some(value) => if value then rec(right) else Some(false)
              case None        => rec(right).filterNot(x => x)
          case _ =>
            tree.tpe.widenTermRefByName match
              case ConstantType(c) => Some(c.value.asInstanceOf[Boolean])
              case _               => None

      rec(expr.asTerm)

  /**
   * A FromExpr[String] that can extract value from concatenated strings if all arguments are compile-time-extractable strings.
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
        case Apply(Select(left, "+"), List(right)) if left.tpe <:< TypeRepr.of[String] && right.tpe <:< TypeRepr.of[String] =>
          rec(left).zip(rec(right)).map(_ + _)
        case _ =>
          tree.tpe.widenTermRefByName match
            case ConstantType(c) => Some(c.value.asInstanceOf[String])
            case _               => None

      rec(expr.asTerm)

  inline def assertCondition[A](
      inline input: A,
      inline cond: Boolean,
      inline message: String
  ): Unit = ${ assertConditionImpl('input, 'cond, 'message) }

  private def assertConditionImpl[A](
      input: Expr[A],
      cond: Expr[Boolean],
      message: Expr[String]
  )(using Quotes): Expr[Unit] =

    import quotes.reflect.*

    val messageValue = message.value.getOrElse("<Unknown message>")
    val condValue = cond.value
      .getOrElse(
        report.errorAndAbort(
          s"""Cannot refine value at compile-time because the predicate cannot be evaluated.
           |This is likely because the condition or the input value isn't fully inlined.
           |Note: Due to a Scala limitation, already-refined types cannot be tested at compile-time (except proven by an `Implication`).
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

  inline def showAST[T](inline value: T): Unit = ${ showASTMacro('value) }
  private def showASTMacro[T](expr: Expr[T])(using Quotes): Expr[Unit] =
    import quotes.reflect.*

    report.info(expr.asTerm.show(using Printer.TreeStructure))
    '{}

end macros
