package io.github.iltotore.iron.macros

import scala.quoted.*
import io.github.iltotore.iron.compileTime.NumConstant

/**
 * Low AST related utils.
 *
 * @param q the metaprogramming information
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

  type DecodingResult[+T] = Either[DecodingFailure, T]
  extension [T](result: DecodingResult[T])
    private def as[U]: DecodingResult[U] = result.asInstanceOf[DecodingResult[U]]

  extension [T: Type](expr: Expr[T])
    /**
     * Decode this expression.
     *
     * @return the value of this expression found at compile time or a [[DecodingFailure]]
     */
    def decode: DecodingResult[T] = ExprDecoder.decodeTerm(expr.asTerm, Map.empty).as[T]

  /**
   * A decoding failure.
   */
  enum DecodingFailure:

    /**
     * A term is not inlined. Note that an `inline` val/def can still not be inlined by the compiler in some cases.
     *
     * @param term the term that is not inlined
     */
    case NotInlined(term: Term)

    /**
     * A definition is not inlined.
     *
     * @param name the name definition
     */
    case DefinitionNotInlined(name: String)

    /**
     * The term could not be fully inlined because it has runtime bindings/depends on runtime definitions.
     *
     * @param defFailures the definitions that the decoder failed to evaluate at compile-time
     */
    case HasBindings(defFailures: List[(String, DecodingFailure)])

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
    case ApplyNotInlined(name: String, parameters: List[DecodingResult[?]])

    case VarArgsNotInlined(args: List[DecodingResult[?]])

    /**
     * A boolean OR is not inlined.
     *
     * @param left  the left operand
     * @param right the right operand
     */
    case OrNotInlined(left: DecodingResult[Boolean], right: Either[DecodingFailure, Boolean])

    /**
     * A boolean AND is not inlined.
     *
     * @param left  the left operand
     * @param right the right operand
     */
    case AndNotInlined(left: DecodingResult[Boolean], right: Either[DecodingFailure, Boolean])

    /**
     * Some part of the decoded String are not inlined. A more specialized version of [[ApplyNotInlined]].
     *
     * @param parts the parts of the String
     */
    case StringPartsNotInlined(parts: List[DecodingResult[String]])

    /**
     * The given String interpolator cannot be inlined.
     */
    case InterpolatorNotInlined(name: String)

    /**
     * An unknown failure.
     */
    case Unknown

    /**
     * Pretty print this failure.
     *
     * @param bodyIdent the identation of the 2nd+ lines
     * @param firstLineIdent the identation of the first line
     * @return a pretty-formatted [[String]] representation of this failure
     */
    def prettyPrint(bodyIdent: Int = 0, firstLineIdent: Int = 0)(using Printer[Tree]): String =
      val unindented = this match
        case NotInlined(term)           => s"Term not inlined: ${term.show}"
        case DefinitionNotInlined(name) => s"Definition not inlined: $name. Only vals and zero-arg def can be inlined."
        case HasBindings(defFailures) =>
          val failures = defFailures
            .map((n, b) => s"- $n:\n${b.prettyPrint(2, 2)}")
            .mkString("\n")

          s"Term depends on runtime definitions:\n$failures"
        case HasStatements(block) => s"Block has statements: ${block.show}"
        case ApplyNotInlined(name, operands) =>
          val errors = operands
            .zipWithIndex
            .collect:
              case (Left(failure), i) => s"Arg $i:\n${failure.prettyPrint(2, 2)}"
            .mkString("\n\n")

          s"Some arguments of `$name` are not inlined:\n$errors"

        case VarArgsNotInlined(args) =>
          val errors = args
            .zipWithIndex
            .collect:
              case (Left(failure), i) => s"Arg $i:\n${failure.prettyPrint(2, 2)}"
            .mkString("\n\n")

          s"Some varargs are not inlined:\n$errors"

        case OrNotInlined(left, right) =>
          s"""Non-inlined boolean or. The following patterns are evaluable at compile-time:
             |- <inlined value> || <inlined value>
             |- <inlined value> || true
             |- true || <inlined value>
             |
             |Left member:
             |${left.fold(_.prettyPrint(2, 2), "  " + _)}
             |
             |Right member:
             |${right.fold(_.prettyPrint(2, 2), "  " + _)}""".stripMargin

        case AndNotInlined(left, right) =>
          s"""Non-inlined boolean and. The following patterns are evaluable at compile-time:
             |- <inlined value> && <inlined value>
             |- <inlined value> && false
             |- false && <inlined value>
             |
             |Left member:
             |${left.fold(_.prettyPrint(2, 2), "  " + _)}
             |
             |Right member:
             |${right.fold(_.prettyPrint(2, 2), "  " + _)}""".stripMargin

        case StringPartsNotInlined(parts) =>
          val errors = parts
            .zipWithIndex
            .collect:
              case (Left(failure), i) => s"Arg $i:\n${failure.prettyPrint(2, 2)}"
            .mkString("\n\n")

          s"String contatenation has non inlined arguments:\n$errors"

        case InterpolatorNotInlined(name) => s"This interpolator is not supported: $name. Only `s` and `raw` are supported."

        case Unknown => "Unknown reason"

      " " * firstLineIdent + unindented.replaceAll("(\r\n|\n|\r)", "$1" + " " * bodyIdent)

  object ExprDecoder:

    private val enhancedDecoders: Map[TypeRepr, (Term, Map[String, ?]) => DecodingResult[?]] = Map(
      TypeRepr.of[Boolean] -> decodeBoolean,
      TypeRepr.of[BigDecimal] -> decodeBigDecimal,
      TypeRepr.of[BigInt] -> decodeBigInt,
      TypeRepr.of[List[?]] -> decodeList,
      TypeRepr.of[Set[?]] -> decodeSet,
      TypeRepr.of[String] -> decodeString
    )

    /**
     * Decode a term.
     *
     * @param tree the term to decode
     * @param definitions the decoded definitions in scope
     * @return the value of the given term found at compile time or a [[DecodingFailure]]
     */
    def decodeTerm(tree: Term, definitions: Map[String, ?]): DecodingResult[?] =
      val specializedResult = enhancedDecoders
        .collectFirst:
          case (k, v) if tree.tpe <:< k => v
        .toRight(DecodingFailure.Unknown)
        .flatMap(_.apply(tree, definitions))

      specializedResult match
        case Left(DecodingFailure.Unknown) => decodeUnspecializedTerm(tree, definitions)
        case result                        => result

    /**
     * Decode a term using only unspecialized cases.
     *
     * @param tree        the term to decode
     * @param definitions the decoded definitions in scope
     * @tparam T the expected type of this term used as implicit cast for convenience
     * @return the value of the given term found at compile time or a [[DecodingFailure]]
     */
    def decodeUnspecializedTerm(tree: Term, definitions: Map[String, ?]): DecodingResult[?] =
      tree match
        case block @ Block(stats, e) => if stats.isEmpty then decodeTerm(e, definitions) else Left(DecodingFailure.HasStatements(block))

        case Inlined(_, bindings, e) =>
          val (failures, values) = bindings
            .map[(String, DecodingResult[?])](b => (b.name, decodeBinding(b, definitions)))
            .partitionMap:
              case (name, Right(value))  => Right((name, value))
              case (name, Left(failure)) => Left((name, failure))

          (failures, decodeTerm(e, definitions ++ values.toMap)) match
            case (_, Right(value)) =>
              Right(value)
            case (Nil, Left(failure)) => Left(failure)
            case (failures, Left(_))  => Left(DecodingFailure.HasBindings(failures))

        case Apply(Select(left, "=="), List(right)) => (decodeTerm(left, definitions), decodeTerm(right, definitions)) match
            case (Right(leftValue), Right(rightValue)) => Right((leftValue == rightValue))
            case (leftResult, rightResult)             => Left(DecodingFailure.ApplyNotInlined("==", List(leftResult, rightResult)))

        case Apply(Select(leftOperand, name), operands) =>
          val rightResults = operands.map(decodeTerm(_, definitions))

          val allResults = decodeTerm(leftOperand, definitions) match
            case Left(DecodingFailure.ApplyNotInlined(n, leftResults)) if n == name =>
              leftResults ++ rightResults
            case leftResult =>
              leftResult +: rightResults

          Left(DecodingFailure.ApplyNotInlined(name, allResults))

        case Repeated(terms, _) =>
          var hasFailure = false
          val results =
            for term <- terms yield
              val result = decodeTerm(term, definitions)
              if result.isLeft then hasFailure = true
              result

          if hasFailure then Left(DecodingFailure.VarArgsNotInlined(results))
          else Right(results.map(_.getOrElse((???): String)))

        case Typed(e, _) => decodeTerm(e, definitions)

        case _ =>
          tree.tpe.widenTermRefByName match
            case ConstantType(c) => Right(c.value)
            case _ => tree match
                case Ident(name) => definitions
                    .get(name)
                    .toRight(DecodingFailure.NotInlined(tree))

                case _ => Left(DecodingFailure.NotInlined(tree))

    /**
     * Decode a binding/definition.
     *
     * @param definition the definition to decode
     * @param definitions the definitions already decoded in scope
     * @tparam T the expected type of this term used as implicit cast for convenience
     * @return the value of the given definition found at compile time or a [[DecodingFailure]]
     */
    def decodeBinding(definition: Definition, definitions: Map[String, ?]): DecodingResult[?] = definition match
      case ValDef(name, tpeTree, Some(term))      => decodeTerm(term, definitions)
      case DefDef(name, Nil, tpeTree, Some(term)) => decodeTerm(term, definitions)
      case _                                      => Left(DecodingFailure.DefinitionNotInlined(definition.name))

    /**
     * Decode a [[Boolean]] term using only [[Boolean]]-specific cases.
     *
     * @param term        the term to decode
     * @param definitions the decoded definitions in scope
     * @return the value of the given term found at compile time or a [[DecodingFailure]]
     */
    def decodeBoolean(term: Term, definitions: Map[String, ?]): DecodingResult[?] = term match
      case Apply(Select(left, "||"), List(right)) if left.tpe <:< TypeRepr.of[Boolean] && right.tpe <:< TypeRepr.of[Boolean] => // OR
        (decodeTerm(left, definitions).as[Boolean], decodeTerm(right, definitions).as[Boolean]) match
          case (Right(true), _)                      => Right(true)
          case (_, Right(true))                      => Right(true)
          case (Right(leftValue), Right(rightValue)) => Right(leftValue || rightValue)
          case (leftResult, rightResult)             => Left(DecodingFailure.OrNotInlined(leftResult, rightResult))

      case Apply(Select(left, "&&"), List(right)) if left.tpe <:< TypeRepr.of[Boolean] && right.tpe <:< TypeRepr.of[Boolean] => // AND
        (decodeTerm(left, definitions).as[Boolean], decodeTerm(right, definitions).as[Boolean]) match
          case (Right(false), _)                     => Right(false)
          case (_, Right(false))                     => Right(false)
          case (Right(leftValue), Right(rightValue)) => Right(leftValue && rightValue)
          case (leftResult, rightResult)             => Left(DecodingFailure.AndNotInlined(leftResult, rightResult))

      case _ => Left(DecodingFailure.Unknown)

    /**
     * Decode a [[String]] term using only [[String]]-specific cases.
     *
     * @param term        the term to decode
     * @param definitions the decoded definitions in scope
     * @return the value of the given term found at compile time or a [[DecodingFailure]]
     */
    def decodeString(term: Term, definitions: Map[String, ?]): DecodingResult[String] = term match
      case Apply(Select(left, "+"), List(right)) if left.tpe <:< TypeRepr.of[String] && right.tpe <:< TypeRepr.of[String] =>
        (decodeTerm(left, definitions).as[String], decodeTerm(right, definitions).as[String]) match
          case (Right(leftValue), Right(rightValue)) => Right(leftValue + rightValue)
          case (Left(DecodingFailure.StringPartsNotInlined(lparts)), Left(DecodingFailure.StringPartsNotInlined(rparts))) =>
            Left(DecodingFailure.StringPartsNotInlined(lparts ++ rparts))
          case (Left(DecodingFailure.StringPartsNotInlined(lparts)), rightResult) =>
            Left(DecodingFailure.StringPartsNotInlined(lparts :+ rightResult))
          case (leftResult, Left(DecodingFailure.StringPartsNotInlined(rparts))) =>
            Left(DecodingFailure.StringPartsNotInlined(leftResult +: rparts))
          case (leftResult, rightResult) => Left(DecodingFailure.StringPartsNotInlined(List(leftResult, rightResult)))

      case _ => Left(DecodingFailure.Unknown)

    /**
     * Decode a [[BigInt]] term using only [[BigInt]]-specific cases.
     *
     * @param term        the term to decode
     * @param definitions the decoded definitions in scope
     * @return the value of the given term found at compile time or a [[DecodingFailure]]
     */
    def decodeBigInt(term: Term, definitions: Map[String, ?]): DecodingResult[BigInt] =
      term match
        case Apply(Select(Ident("BigInt"), "apply"), List(value)) =>
          decodeTerm(value, definitions).as[Int | Long].map:
            case x: Int  => BigInt(x)
            case x: Long => BigInt(x)
        case _ => Left(DecodingFailure.Unknown)

    /**
     * Decode a [[BigDecimal]] term using only [[BigDecimal]]-specific cases.
     *
     * @param term        the term to decode
     * @param definitions the decoded definitions in scope
     * @return the value of the given term found at compile time or a [[DecodingFailure]]
     */
    def decodeBigDecimal(term: Term, definitions: Map[String, ?]): DecodingResult[BigDecimal] =
      term match
        case Apply(Select(Ident("BigDecimal"), "apply"), List(value)) =>
          decodeTerm(value, definitions).as[NumConstant].map:
            case x: Int    => BigDecimal(x)
            case x: Long   => BigDecimal(x)
            case x: Float  => BigDecimal(x)
            case x: Double => BigDecimal(x)

        case _ => Left(DecodingFailure.Unknown)

    /**
     * Decode a [[List]] term using only [[List]]-specific cases.
     *
     * @param term        the term to decode
     * @param definitions the decoded definitions in scope
     * @return the value of the given term found at compile time or a [[DecodingFailure]]
     */
    def decodeList(term: Term, definitions: Map[String, ?]): DecodingResult[List[?]] =
      term match
        case Apply(TypeApply(Select(Ident("List"), "apply"), _), List(values)) =>
          decodeTerm(values, definitions).as[List[?]]
        case _ => Left(DecodingFailure.Unknown)

    /**
     * Decode a [[Set]] term using only [[Set]]-specific cases.
     *
     * @param term        the term to decode
     * @param definitions the decoded definitions in scope
     * @return the value of the given term found at compile time or a [[DecodingFailure]]
     */
    def decodeSet(term: Term, definitions: Map[String, ?]): DecodingResult[Set[?]] =
      term match
        case Apply(TypeApply(Select(Ident("Set"), "apply"), _), List(values)) =>
          decodeTerm(values, definitions).as[List[?]].map(_.toSet)
        case _ => Left(DecodingFailure.Unknown)