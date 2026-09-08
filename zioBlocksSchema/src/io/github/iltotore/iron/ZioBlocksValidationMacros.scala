package io.github.iltotore.iron

import _root_.zio.blocks.schema.Validation
import io.github.iltotore.iron.constraint.any.{DescribedAs, Not, StrictEqual}
import io.github.iltotore.iron.constraint.collection.Length
import io.github.iltotore.iron.constraint.numeric.{Greater, GreaterEqual, Less, LessEqual}
import io.github.iltotore.iron.constraint.string.Match as StringMatch

import scala.quoted.*

private[iron] object ZioBlocksValidationMacros:

  def derived[A: Type, C: Type](using Quotes): Expr[ZioBlocksValidation[A, C]] =
    import quotes.reflect.*

    enum Translated:
      case Positive, Negative, NonPositive, NonNegative
      case Set(value: BigDecimal)
      case Range(min: Option[BigDecimal], max: Option[BigDecimal])
      case Pattern(regex: String)
      case Length(min: Option[Int], max: Option[Int])

    def normalized(tpe: TypeRepr): TypeRepr =
      tpe.dealias match
        case AppliedType(tycon, List(inner, _)) if tycon =:= TypeRepr.of[DescribedAs] => normalized(inner)
        case other                                                                   => other

    def intConstant(tpe: TypeRepr): Option[Int] =
      tpe.dealias match
        case ConstantType(IntConstant(value)) => Some(value)
        case _                                => None

    def bigDecimalConstant(tpe: TypeRepr): Option[BigDecimal] =
      tpe.dealias match
        case ConstantType(IntConstant(value))    => Some(BigDecimal(value))
        case ConstantType(LongConstant(value))   => Some(BigDecimal(value))
        case ConstantType(FloatConstant(value))  => Some(BigDecimal(value.toDouble))
        case ConstantType(DoubleConstant(value)) => Some(BigDecimal(value))
        case _                                   => None

    def stringConstant(tpe: TypeRepr): Option[String] =
      tpe.dealias match
        case ConstantType(StringConstant(value)) => Some(value)
        case _                                   => None

    def applied(tpe: TypeRepr, symbol: Symbol): Option[List[TypeRepr]] =
      normalized(tpe) match
        case AppliedType(tycon, arguments) if tycon.typeSymbol == symbol => Some(arguments)
        case _                                                             => None

    def zero(tpe: TypeRepr): Boolean = intConstant(tpe).contains(0)

    def sameBound(left: TypeRepr, right: TypeRepr): Boolean =
      (applied(left, TypeRepr.of[Greater].typeSymbol), applied(right, TypeRepr.of[StrictEqual].typeSymbol)) match
        case (Some(List(leftValue)), Some(List(rightValue))) => leftValue =:= rightValue
        case _                                               => false

    def sameLowerBound(left: TypeRepr, right: TypeRepr): Boolean =
      (applied(left, TypeRepr.of[Less].typeSymbol), applied(right, TypeRepr.of[StrictEqual].typeSymbol)) match
        case (Some(List(leftValue)), Some(List(rightValue))) => leftValue =:= rightValue
        case _                                               => false

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
        tpe match
          case AppliedType(tycon, List(value)) if tycon =:= TypeRepr.of[StrictEqual] =>
            intConstant(value).map(v => Some(v) -> Some(v))
          case AppliedType(tycon, List(value)) if tycon =:= TypeRepr.of[GreaterEqual] =>
            intConstant(value).map(v => Some(v) -> None)
          case AppliedType(tycon, List(value)) if tycon =:= TypeRepr.of[LessEqual] =>
            intConstant(value).map(v => None -> Some(v))
          case AppliedType(tycon, List(value)) if tycon =:= TypeRepr.of[Greater] =>
            intConstant(value).flatMap(v => Option.when(v != Int.MaxValue)(Some(v + 1) -> None))
          case AppliedType(tycon, List(value)) if tycon =:= TypeRepr.of[Less] =>
            intConstant(value).flatMap(v => Option.when(v != Int.MinValue)(None -> Some(v - 1)))
          case OrType(left, right) if sameBound(left, right) =>
            applied(left, TypeRepr.of[Greater].typeSymbol).flatMap(_.headOption).flatMap(intConstant).map(v => Some(v) -> None)
          case OrType(left, right) if sameBound(right, left) =>
            applied(right, TypeRepr.of[Greater].typeSymbol).flatMap(_.headOption).flatMap(intConstant).map(v => Some(v) -> None)
          case OrType(left, right) if sameLowerBound(left, right) =>
            applied(left, TypeRepr.of[Less].typeSymbol).flatMap(_.headOption).flatMap(intConstant).map(v => None -> Some(v))
          case OrType(left, right) if sameLowerBound(right, left) =>
            applied(right, TypeRepr.of[Less].typeSymbol).flatMap(_.headOption).flatMap(intConstant).map(v => None -> Some(v))
          case _ => None

      normalized(tpe) match
        case AppliedType(tycon, List(inner)) if tycon =:= TypeRepr.of[Length] => bound(inner)
        case AndType(left, right)                                              =>
          for
            leftBounds <- lengthBounds(left)
            rightBounds <- lengthBounds(right)
            combined <- combine(leftBounds, rightBounds)
          yield combined
        case other => bound(other)

    def numericTranslation(tpe: TypeRepr): Option[Translated] =
      def inclusive(lower: TypeRepr, equal: TypeRepr): Option[Translated] =
        for
          bound <- applied(lower, TypeRepr.of[Greater].typeSymbol).flatMap(_.headOption).flatMap(intConstant)
          value <- applied(equal, TypeRepr.of[StrictEqual].typeSymbol).flatMap(_.headOption).flatMap(intConstant)
          if bound == value
        yield Translated.Range(Some(BigDecimal(value)), None)

      def inclusiveUpper(upper: TypeRepr, equal: TypeRepr): Option[Translated] =
        for
          bound <- applied(upper, TypeRepr.of[Less].typeSymbol).flatMap(_.headOption).flatMap(intConstant)
          value <- applied(equal, TypeRepr.of[StrictEqual].typeSymbol).flatMap(_.headOption).flatMap(intConstant)
          if bound == value
        yield Translated.Range(None, Some(BigDecimal(value)))

      def merge(left: Translated, right: Translated): Option[Translated] =
        (left, right) match
          case (Translated.Range(minA, maxA), Translated.Range(minB, maxB)) =>
            Some(Translated.Range(minA.orElse(minB), maxA.orElse(maxB)))
          case _ => None

      normalized(tpe) match
        case AppliedType(tycon, List(value)) if tycon =:= TypeRepr.of[StrictEqual] =>
          intConstant(value).map(v => Translated.Set(BigDecimal(v)))

        case AppliedType(tycon, List(value)) if tycon =:= TypeRepr.of[GreaterEqual] =>
          intConstant(value).map(v => Translated.Range(Some(BigDecimal(v)), None))

        case AppliedType(tycon, List(value)) if tycon =:= TypeRepr.of[LessEqual] =>
          intConstant(value).map(v => Translated.Range(None, Some(BigDecimal(v))))

        case AppliedType(tycon, List(value)) if tycon =:= TypeRepr.of[Greater] && zero(value) =>
          Some(Translated.Positive)

        case AppliedType(tycon, List(value)) if tycon =:= TypeRepr.of[Less] && zero(value) =>
          Some(Translated.Negative)

        case AppliedType(tycon, List(value)) if tycon =:= TypeRepr.of[Greater] =>
          intConstant(value).map(v => Translated.Range(Some(BigDecimal(v)), None))

        case AppliedType(tycon, List(value)) if tycon =:= TypeRepr.of[Less] =>
          intConstant(value).map(v => Translated.Range(None, Some(BigDecimal(v))))

        case OrType(left, right) if sameBound(left, right) && applied(left, TypeRepr.of[Greater].typeSymbol).flatMap(_.headOption).exists(zero) =>
          Some(Translated.NonNegative)

        case OrType(left, right) if sameBound(right, left) && applied(right, TypeRepr.of[Greater].typeSymbol).flatMap(_.headOption).exists(zero) =>
          Some(Translated.NonNegative)

        case OrType(left, right) if sameLowerBound(left, right) && applied(left, TypeRepr.of[Less].typeSymbol).flatMap(_.headOption).exists(zero) =>
          Some(Translated.NonPositive)

        case OrType(left, right) if sameLowerBound(right, left) && applied(right, TypeRepr.of[Less].typeSymbol).flatMap(_.headOption).exists(zero) =>
          Some(Translated.NonPositive)

        case OrType(left, right) =>
          inclusive(left, right)
            .orElse(inclusive(right, left))
            .orElse(inclusiveUpper(left, right))
            .orElse(inclusiveUpper(right, left))

        case AndType(left, right) =>
          for
            leftTranslated  <- numericTranslation(left)
            rightTranslated <- numericTranslation(right)
            merged          <- merge(leftTranslated, rightTranslated)
          yield merged

        case _ => None

    def stringTranslation(tpe: TypeRepr): Option[Translated] =
      normalized(tpe) match
        case AppliedType(tycon, List(pattern)) if tycon =:= TypeRepr.of[StringMatch] =>
          stringConstant(pattern).map(Translated.Pattern.apply)
        case AppliedType(tycon, List(inner)) if tycon =:= TypeRepr.of[Not] =>
          lengthBounds(inner).collect:
            case (Some(0), Some(0)) => Translated.Length(Some(1), None)
        case other =>
          lengthBounds(other).map(Translated.Length.apply)

    val base = TypeRepr.of[A].dealias
    val translated =
      if base =:= TypeRepr.of[String] then stringTranslation(TypeRepr.of[C])
      else if
        base =:= TypeRepr.of[Int] ||
        base =:= TypeRepr.of[Long] ||
        base =:= TypeRepr.of[Float] ||
        base =:= TypeRepr.of[Double] ||
        base =:= TypeRepr.of[BigInt] ||
        base =:= TypeRepr.of[BigDecimal]
      then numericTranslation(TypeRepr.of[C])
      else None

    val validationExpr: Expr[Option[Validation[A]]] =
      def rangeExpr(min: Option[BigDecimal], max: Option[BigDecimal]): Expr[Option[Validation[A]]] =
        val base = TypeRepr.of[A].dealias
        if base =:= TypeRepr.of[Int] then
          val minExpr = min.flatMap(v => Option.when(v.isValidInt)(v.toInt)) match
            case Some(value) => '{ Some(${ Expr(value) }) }
            case None        => '{ None }
          val maxExpr = max.flatMap(v => Option.when(v.isValidInt)(v.toInt)) match
            case Some(value) => '{ Some(${ Expr(value) }) }
            case None        => '{ None }
          '{ Some(Validation.Numeric.Range($minExpr, $maxExpr).asInstanceOf[Validation[A]]) }
        else if base =:= TypeRepr.of[Long] then
          val minExpr = min.flatMap(v => Option.when(v.isValidLong)(v.toLong)) match
            case Some(value) => '{ Some(${ Expr(value) }) }
            case None        => '{ None }
          val maxExpr = max.flatMap(v => Option.when(v.isValidLong)(v.toLong)) match
            case Some(value) => '{ Some(${ Expr(value) }) }
            case None        => '{ None }
          '{ Some(Validation.Numeric.Range($minExpr, $maxExpr).asInstanceOf[Validation[A]]) }
        else if base =:= TypeRepr.of[Double] then
          val minExpr = min.map(_.toDouble) match
            case Some(value) => '{ Some(${ Expr(value) }) }
            case None        => '{ None }
          val maxExpr = max.map(_.toDouble) match
            case Some(value) => '{ Some(${ Expr(value) }) }
            case None        => '{ None }
          '{ Some(Validation.Numeric.Range($minExpr, $maxExpr).asInstanceOf[Validation[A]]) }
        else if base =:= TypeRepr.of[Float] then
          val minExpr = min.map(_.toFloat) match
            case Some(value) => '{ Some(${ Expr(value) }) }
            case None        => '{ None }
          val maxExpr = max.map(_.toFloat) match
            case Some(value) => '{ Some(${ Expr(value) }) }
            case None        => '{ None }
          '{ Some(Validation.Numeric.Range($minExpr, $maxExpr).asInstanceOf[Validation[A]]) }
        else if base =:= TypeRepr.of[BigInt] then
          val minExpr = min.map(_.toBigInt) match
            case Some(value) => '{ Some(${ Expr(value) }) }
            case None        => '{ None }
          val maxExpr = max.map(_.toBigInt) match
            case Some(value) => '{ Some(${ Expr(value) }) }
            case None        => '{ None }
          '{ Some(Validation.Numeric.Range($minExpr, $maxExpr).asInstanceOf[Validation[A]]) }
        else
          val minExpr = min match
            case Some(value) => '{ Some(${ Expr(value) }) }
            case None        => '{ None }
          val maxExpr = max match
            case Some(value) => '{ Some(${ Expr(value) }) }
            case None        => '{ None }
          '{ Some(Validation.Numeric.Range($minExpr, $maxExpr).asInstanceOf[Validation[A]]) }

      def setExpr(value: BigDecimal): Expr[Option[Validation[A]]] =
        val base = TypeRepr.of[A].dealias
        if base =:= TypeRepr.of[Int] && value.isValidInt then
          '{ Some(Validation.Numeric.Set(Set(${ Expr(value.toInt) })).asInstanceOf[Validation[A]]) }
        else if base =:= TypeRepr.of[Long] && value.isValidLong then
          '{ Some(Validation.Numeric.Set(Set(${ Expr(value.toLong) })).asInstanceOf[Validation[A]]) }
        else
          '{ Some(Validation.Numeric.Set(Set(${ Expr(value) })).asInstanceOf[Validation[A]]) }

      translated match
      case Some(Translated.Positive)    => '{ Some(Validation.Numeric.Positive.asInstanceOf[Validation[A]]) }
      case Some(Translated.Negative)    => '{ Some(Validation.Numeric.Negative.asInstanceOf[Validation[A]]) }
      case Some(Translated.NonPositive) => '{ Some(Validation.Numeric.NonPositive.asInstanceOf[Validation[A]]) }
      case Some(Translated.NonNegative) => '{ Some(Validation.Numeric.NonNegative.asInstanceOf[Validation[A]]) }
      case Some(Translated.Set(value)) =>
        setExpr(value)
      case Some(Translated.Range(min, max)) =>
        rangeExpr(min, max)
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
