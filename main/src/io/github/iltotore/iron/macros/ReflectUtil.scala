package io.github.iltotore.iron.macros

import io.github.iltotore.iron.compileTime.NumConstant

import scala.quoted.*

/**
 * Low AST related utils.
 *
 * @param _quotes the metaprogramming information
 * @tparam Q the type of `_quotes` to ensure the path is valid to import.
 */
transparent inline def reflectUtil[Q <: Quotes & Singleton](using inline q: Q): ReflectUtil[Q] = new ReflectUtil[Q]

/**
 * Low AST related utils.
 *
 * @param _quotes the metaprogramming information
 * @tparam Q the type of `_quotes` to ensure the path is valid to import.
 */
class ReflectUtil[Q <: Quotes & Singleton](using val _quotes: Q):

  import _quotes.reflect.*

  /**
   * A decoding failure.
   */
  enum DecodingFailure:

    /**
     * Unknown failure.
     */
    case Unknown

    /**
     * A term is not inlined. Note that an `inline` val/def can still not be inlined by the compiler in some cases.
     *
     * @param term the term that is not inlined
     */
    case NotInlined(term: Term)

    /**
     * The term could not be fully inlined because it has runtime bindings/depends on runtime definitions.
     *
     * @param defFailures the definitions that the decoder failed to evaluate at compile-time
     */
    case HasBindings(defFailures: List[DecodingFailure])

    /**
     * The block has possibly side-effecting statements.
     *
     * @param block the block containing statements
     */
    case HasStatements(block: Block)

    /**
     * A method application is not inlined, probably due to some parameters not being inlined.
     *
     * @param parameters the list of decoded parameters, whether an failure or a value of unknown type
     */
    case ApplyNotInlined(name: String, parameters: List[Either[DecodingFailure, ?]])

    /**
     * A boolean OR is not inlined.
     *
     * @param left the left operand
     * @param right the right operand
     */
    case OrNotInlined(left: Either[DecodingFailure, Boolean], right: Either[DecodingFailure, Boolean])

    /**
     * A boolean AND is not inlined.
     *
     * @param left the left operand
     * @param right the right operand
     */
    case AndNotInlined(left: Either[DecodingFailure, Boolean], right: Either[DecodingFailure, Boolean])

    /**
     * Some part of the decoded String are not inlined. A more specialized version of [[ApplyNotInlined]].
     *
     * @param parts the parts of the String
     */
    case StringPartsNotInlined(parts: List[Either[DecodingFailure, String]])

    /**
     * Pretty print this failure.
     *
     * @param bodyIdent the identation of the 2nd+ lines
     * @param firstLineIdent the identation of the first line
     * @return a pretty-formatted [[String]] representation of this failure
     */
    def prettyPrint(bodyIdent: Int = 0, firstLineIdent: Int = 0): String =
      val unindented = this match
        case Unknown => "Unknown"
        case NotInlined(term) => s"Term not inlined: ${term.show}"
        case HasBindings(defFailures) =>
          val failures = defFailures
            .map(_.prettyPrint(2))
            .mkString("- ", "\n- ", "")

          s"Term depends on runtime definitions:\n$failures"
        case HasStatements(block) => s"Block has statements: ${block.show}"
        case ApplyNotInlined(name, operands) =>
          val errors = operands
            .zipWithIndex
            .collect:
              case (Left(failure), i) => s"Arg $i:\n${failure.prettyPrint(2, 2)}"
            .mkString("\n\n")

          s"Some arguments of `$name` are not inlined:\n$errors"

        case OrNotInlined(left, right) =>
          s"""Non-inlined boolean or. The following patterns are evaluable at compile-time:
             |- <inlined value> || <inlined value>
             |- <inlined value> || true
             |- true || <inlined value>
             |
             |Left member:
             |${left.fold(_.prettyPrint(2, 2), _.toString)}
             |
             |Right member:
             |${right.fold(_.prettyPrint(2, 2), _.toString)}""".stripMargin

        case AndNotInlined(left, right) =>
          s"""Non-inlined boolean or. The following patterns are evaluable at compile-time:
             |- <inlined value> || <inlined value>
             |- <inlined value> || true
             |- true || <inlined value>
             |
             |Left member:
             |${left.fold(_.prettyPrint(2, 2), _.toString)}
             |
             |Right member:
             |${right.fold(_.prettyPrint(2, 2), _.toString)}""".stripMargin

        case StringPartsNotInlined(parts) =>
          val errors = parts
            .zipWithIndex
              .collect:
                case (Left(failure), i) => s"Arg $i:\n${failure.prettyPrint(2, 2)}"
              .mkString("\n\n")

          s"String contatenation as non inlined arguments:\n$errors"

      " " * firstLineIdent + unindented.replaceAll("(\r\n|\n|\r)", "$1" + " " * bodyIdent)

    override def toString: String = prettyPrint()

  /**
   * A compile-time [[Expr]] decoder. Like [[FromExpr]] with more fine-grained errors.
   *
   * @tparam T the type of the expression to decode
   */
  trait ExprDecoder[T]:

    /**
     * Decode the given expression.
     *
     * @param expr the expression to decode
     * @return the value decoded from [[expr]] or a [[DecodingFailure]] instead
     */
    def decode(expr: Expr[T]): Either[DecodingFailure, T]

  object ExprDecoder:

    given [T](using fromExpr: FromExpr[T]): ExprDecoder[T] with

      override def decode(expr: Expr[T]): Either[DecodingFailure, T] =
        fromExpr.unapply(expr).toRight(DecodingFailure.Unknown)

    private class PrimitiveExprDecoder[T <: NumConstant | Byte | Short | Boolean | String] extends ExprDecoder[T]:

      private def getDefDecodingFailure(definition: Definition): Option[DecodingFailure] = definition match
        case ValDef(_, _, Some(term)) => decodeTerm(term).left.toOption
        case DefDef(_, Nil, _, Some(term)) => decodeTerm(term).left.toOption
        case _ => None

      def decodeTerm(tree: Term): Either[DecodingFailure, T] = tree match
        case block@Block(stats, e) => if stats.isEmpty then decodeTerm(e) else Left(DecodingFailure.HasStatements(block))

        case Inlined(_, bindings, e) =>
          if bindings.isEmpty then decodeTerm(e)
          else Left(DecodingFailure.HasBindings(bindings.flatMap(getDefDecodingFailure)))

        case Typed(e, _) => decodeTerm(e)
        case Apply(Select(leftOperand, name), operands) =>
          val rightResults = operands.map(decodeTerm)

          val allResults = decodeTerm(leftOperand) match
            case Left(DecodingFailure.ApplyNotInlined(n, leftResults)) if n == name =>
              leftResults ++ rightResults
            case leftResult =>
              leftResult +: rightResults

          Left(DecodingFailure.ApplyNotInlined(name, allResults))

        case ref: Ref => Left(DecodingFailure.NotInlined(ref))
        case _ =>
          tree.tpe.widenTermRefByName match
            case ConstantType(c) => Right(c.value.asInstanceOf[T])
            case _ => Left(DecodingFailure.Unknown)

      override def decode(expr: Expr[T]): Either[DecodingFailure, T] =
        decodeTerm(expr.asTerm)

    given [T <: NumConstant]: ExprDecoder[T] = new PrimitiveExprDecoder[T]

    /**
     * A ExprDecoder[Boolean] that can extract value from partially inlined || and
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
    given ExprDecoder[Boolean] = new PrimitiveExprDecoder[Boolean]:

      override def decodeTerm(tree: Term): Either[DecodingFailure, Boolean] = tree match
        case Apply(Select(left, "||"), List(right)) if left.tpe <:< TypeRepr.of[Boolean] && right.tpe <:< TypeRepr.of[Boolean] => // OR
          (decodeTerm(left), decodeTerm(right)) match
            case (Right(true), _) => Right(true)
            case (_, Right(true)) => Right(true)
            case (Right(leftValue), Right(rightValue)) => Right(leftValue || rightValue)
            case (leftResult, rightResult)             => Left(DecodingFailure.OrNotInlined(leftResult, rightResult))

        case Apply(Select(left, "&&"), List(right)) if left.tpe <:< TypeRepr.of[Boolean] && right.tpe <:< TypeRepr.of[Boolean] => // AND
          (decodeTerm(left), decodeTerm(right)) match
            case (Right(false), _) => Right(false)
            case (_, Right(false)) => Right(false)
            case (Right(leftValue), Right(rightValue)) => Right(leftValue && rightValue)
            case (leftResult, rightResult)             => Left(DecodingFailure.AndNotInlined(leftResult, rightResult))
            
        case _ => super.decodeTerm(tree)
      
    given ExprDecoder[String] = new PrimitiveExprDecoder[String]:

      override def decodeTerm(tree: Term): Either[DecodingFailure, String] = tree match
        case Apply(Select(left, "+"), List(right)) if left.tpe <:< TypeRepr.of[String] && right.tpe <:< TypeRepr.of[String] =>
          (decodeTerm(left), decodeTerm(right)) match
            case (Right(leftValue), Right(rightValue)) => Right(leftValue + rightValue)
            case (Left(DecodingFailure.StringPartsNotInlined(lparts)), Left(DecodingFailure.StringPartsNotInlined(rparts))) =>
              Left(DecodingFailure.StringPartsNotInlined(lparts ++ rparts))
            case (Left(DecodingFailure.StringPartsNotInlined(lparts)), rightResult) =>
              Left(DecodingFailure.StringPartsNotInlined(lparts :+ rightResult))
            case (leftResult, Left(DecodingFailure.StringPartsNotInlined(rparts))) =>
              Left(DecodingFailure.StringPartsNotInlined(leftResult +: rparts))
            case (leftResult, rightResult) => Left(DecodingFailure.StringPartsNotInlined(List(leftResult, rightResult)))

        case _ => super.decodeTerm(tree)