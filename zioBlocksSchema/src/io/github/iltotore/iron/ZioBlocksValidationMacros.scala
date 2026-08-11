package io.github.iltotore.iron

import _root_.zio.blocks.schema.Validation
import io.github.iltotore.iron.constraint.any.{DescribedAs, Not, StrictEqual}
import io.github.iltotore.iron.constraint.collection.Length
import io.github.iltotore.iron.constraint.numeric.{Greater, Less}
import io.github.iltotore.iron.constraint.string.Match as StringMatch

import scala.quoted.*

private[iron] object ZioBlocksValidationMacros:

  def derived[A: Type, C: Type](using Quotes): Expr[ZioBlocksValidation[A, C]] =
    import quotes.reflect.*

    enum Translated:
      case Positive, Negative, NonPositive, NonNegative
      case Pattern(regex: String)
      case Length(min: Option[Int], max: Option[Int])

    val describedAs = TypeRepr.of[DescribedAs[?, ?]].typeSymbol
    val not = TypeRepr.of[Not[?]].typeSymbol
    val strictEqual = TypeRepr.of[StrictEqual[?]].typeSymbol
    val greater = TypeRepr.of[Greater[?]].typeSymbol
    val less = TypeRepr.of[Less[?]].typeSymbol
    val length = TypeRepr.of[Length[?]].typeSymbol
    val matches = TypeRepr.of[StringMatch[?]].typeSymbol

    def normalized(tpe: TypeRepr): TypeRepr =
      tpe.dealias match
        case AppliedType(tycon, List(inner, _)) if tycon.typeSymbol == describedAs => normalized(inner)
        case other                                                                 => other

    def intConstant(tpe: TypeRepr): Option[Int] =
      tpe.dealias match
        case ConstantType(IntConstant(value)) => Some(value)
        case _                                => None

    def stringConstant(tpe: TypeRepr): Option[String] =
      tpe.dealias match
        case ConstantType(StringConstant(value)) => Some(value)
        case _                                   => None

    def applied(tpe: TypeRepr, symbol: Symbol): Option[List[TypeRepr]] =
      normalized(tpe) match
        case AppliedType(tycon, arguments) if tycon.typeSymbol == symbol => Some(arguments)
        case _                                                           => None

    def sameBound(left: TypeRepr, right: TypeRepr): Boolean =
      (applied(left, greater), applied(right, strictEqual)) match
        case (Some(List(leftValue)), Some(List(rightValue))) => leftValue =:= rightValue
        case _                                               => false

    def sameLowerBound(left: TypeRepr, right: TypeRepr): Boolean =
      (applied(left, less), applied(right, strictEqual)) match
        case (Some(List(leftValue)), Some(List(rightValue))) => leftValue =:= rightValue
        case _                                               => false

    def zero(tpe: TypeRepr): Boolean = intConstant(tpe).contains(0)

    def numericValidation(tpe: TypeRepr): Option[Translated] =
      normalized(tpe) match
        case AppliedType(tycon, List(value)) if tycon.typeSymbol == greater && zero(value)                              => Some(Translated.Positive)
        case AppliedType(tycon, List(value)) if tycon.typeSymbol == less && zero(value)                                 => Some(Translated.Negative)
        case OrType(left, right) if sameBound(left, right) && applied(left, greater).flatMap(_.headOption).exists(zero) =>
          Some(Translated.NonNegative)
        case OrType(left, right) if sameBound(right, left) && applied(right, greater).flatMap(_.headOption).exists(zero) =>
          Some(Translated.NonNegative)
        case OrType(left, right) if sameLowerBound(left, right) && applied(left, less).flatMap(_.headOption).exists(zero) =>
          Some(Translated.NonPositive)
        case OrType(left, right) if sameLowerBound(right, left) && applied(right, less).flatMap(_.headOption).exists(zero) =>
          Some(Translated.NonPositive)
        case _ => None

    def lengthBounds(tpe: TypeRepr): Option[(Option[Int], Option[Int])] =
      def combine(
          left: (Option[Int], Option[Int]),
          right: (Option[Int], Option[Int])
      ): Option[(Option[Int], Option[Int])] =
        val min = (left._1, right._1) match
          case (Some(a), Some(b)) => Some(math.max(a, b))
          case (value, None)      => value
          case (None, value)      => value
        val max = (left._2, right._2) match
          case (Some(a), Some(b)) => Some(math.min(a, b))
          case (value, None)      => value
          case (None, value)      => value
        Option.when(min.forall(minValue => max.forall(maxValue => minValue <= maxValue)))(min -> max)

      def bound(tpe: TypeRepr): Option[(Option[Int], Option[Int])] =
        normalized(tpe) match
          case AppliedType(tycon, List(value)) if tycon.typeSymbol == strictEqual =>
            intConstant(value).map(v => Some(v) -> Some(v))
          case AppliedType(tycon, List(value)) if tycon.typeSymbol == greater =>
            intConstant(value).flatMap(v => Option.when(v != Int.MaxValue)(Some(v + 1) -> None))
          case AppliedType(tycon, List(value)) if tycon.typeSymbol == less =>
            intConstant(value).flatMap(v => Option.when(v != Int.MinValue)(None -> Some(v - 1)))
          case OrType(left, right) if sameBound(left, right) =>
            applied(left, greater).flatMap(_.headOption).flatMap(v => intConstant(v).map(Some(_) -> None))
          case OrType(left, right) if sameBound(right, left) =>
            applied(right, greater).flatMap(_.headOption).flatMap(v => intConstant(v).map(Some(_) -> None))
          case OrType(left, right) if sameLowerBound(left, right) =>
            applied(left, less).flatMap(_.headOption).flatMap(v => intConstant(v).map(None -> Some(_)))
          case OrType(left, right) if sameLowerBound(right, left) =>
            applied(right, less).flatMap(_.headOption).flatMap(v => intConstant(v).map(None -> Some(_)))
          case _ => None

      normalized(tpe) match
        case AppliedType(tycon, List(inner)) if tycon.typeSymbol == length => bound(inner)
        case AndType(left, right)                                          =>
          for
            leftBounds <- lengthBounds(left)
            rightBounds <- lengthBounds(right)
            combined <- combine(leftBounds, rightBounds)
          yield combined
        case _ => None

    def stringValidation(tpe: TypeRepr): Option[Translated] =
      normalized(tpe) match
        case AppliedType(tycon, List(pattern)) if tycon.typeSymbol == matches =>
          stringConstant(pattern).map(Translated.Pattern.apply)
        case AppliedType(tycon, List(inner)) if tycon.typeSymbol == not =>
          lengthBounds(inner).collect:
            case (Some(0), Some(0)) => Translated.Length(Some(1), None)
        case _ => lengthBounds(tpe).map(Translated.Length.apply)

    val base = TypeRepr.of[A].dealias
    val translated =
      if base =:= TypeRepr.of[String] then stringValidation(TypeRepr.of[C])
      else if
        base =:= TypeRepr.of[Int] ||
        base =:= TypeRepr.of[Long] ||
        base =:= TypeRepr.of[Float] ||
        base =:= TypeRepr.of[Double] ||
        base =:= TypeRepr.of[BigInt] ||
        base =:= TypeRepr.of[BigDecimal]
      then numericValidation(TypeRepr.of[C])
      else None

    val validationExpr: Expr[Option[Validation[A]]] = translated match
      case Some(Translated.Positive)       => '{ Some(Validation.Numeric.Positive.asInstanceOf[Validation[A]]) }
      case Some(Translated.Negative)       => '{ Some(Validation.Numeric.Negative.asInstanceOf[Validation[A]]) }
      case Some(Translated.NonPositive)    => '{ Some(Validation.Numeric.NonPositive.asInstanceOf[Validation[A]]) }
      case Some(Translated.NonNegative)    => '{ Some(Validation.Numeric.NonNegative.asInstanceOf[Validation[A]]) }
      case Some(Translated.Pattern(regex)) =>
        '{ Some(Validation.String.Pattern(${ Expr(regex) }).asInstanceOf[Validation[A]]) }
      case Some(Translated.Length(min, max)) =>
        val minExpr = min match
          case Some(value) => '{ Some(${ Expr(value) }) }
          case None        => '{ None }
        val maxExpr = max match
          case Some(value) => '{ Some(${ Expr(value) }) }
          case None        => '{ None }
        '{ Some(Validation.String.Length($minExpr, $maxExpr).asInstanceOf[Validation[A]]) }
      case None => '{ None }

    '{
      new ZioBlocksValidation[A, C]:
        val validation: Option[Validation[A]] = $validationExpr
    }
