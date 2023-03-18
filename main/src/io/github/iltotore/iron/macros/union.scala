package io.github.iltotore.iron.macros

import io.github.iltotore.iron.{Constraint, Implication, ==>}
import io.github.iltotore.iron.internal.*
import io.github.iltotore.iron.internal.Validation.{Valid, Invalid}
import scala.quoted.*

/**
 * Internal macros for union types
 */
object union:

  /**
   * Typeclass only implemented by union types. Used as evidence in implicit methods.
   *
   * @tparam A the underlying union type.
   */
  final class IsUnion[A]

  // the only instance for IsUnion used to avoid overhead
  val isUnionSingleton: IsUnion[Any] = new IsUnion

  object IsUnion:
    transparent inline given [A]: IsUnion[A] = ${ isUnionImpl[A] }

    private def isUnionImpl[A](using Quotes, Type[A]): Expr[IsUnion[A]] =
      import quotes.reflect.*
      val tpe: TypeRepr = TypeRepr.of[A]
      tpe.dealias match
        case o: OrType => ('{ isUnionSingleton.asInstanceOf[IsUnion[A]] }).asExprOf[IsUnion[A]]
        case other     => report.errorAndAbort(s"${tpe.show} is not a Union")

  transparent inline def unionCond[A, C](value: A): Boolean = ${ unionCondImpl[A, C]('value) }

  private def unionCondImpl[A, C](value: Expr[A])(using Quotes, Type[A], Type[C]): Expr[Boolean] =

    import quotes.reflect.*

    val aTpe = TypeRepr.of[A]

    val constraintTpe = TypeRepr.of[Constraint]

    def rec(tpe: TypeRepr): Validation[TypeRepr, Expr[Boolean]] =
      tpe.dealias match
        case OrType(left, right) =>
          rec(left)
            .accumulate(rec(right))
            .map((leftResult, rightResult) => '{ $leftResult || $rightResult })

        case t =>
          val implTpe = constraintTpe.appliedTo(List(aTpe, t))

          Implicits.search(implTpe) match
            case iss: ImplicitSearchSuccess =>
              val implTerm = iss.tree
              Valid(Apply(Select.unique(implTerm, "test"), List(value.asTerm)).asExprOf[Boolean])

            case isf: ImplicitSearchFailure => Invalid(List(t))

    rec(TypeRepr.of[C]) match
      case Valid(value) => value
      case Invalid(errors) =>
        val missingTypes = errors.map(tpe => s"- ${tpe.show}").mkString("\n")
        compileTimeError(s"""|Missing given instances of Constraint[${aTpe.show}, ...] in union for types:
                             |$missingTypes""".stripMargin)

  /**
   * Constraint message for union type.
   *
   * @tparam A the input type (like in `Constraint[A, C]`).
   * @tparam C the constraint type (like in `Constraint[A, C])`. Should be an union.
   * @return the generated message for this constraint union.
   */
  inline def unionMessage[A, C]: String = ${ unionMessageImpl[A, C] }

  private def unionMessageImpl[A, C](using Quotes, Type[A], Type[C]): Expr[String] =
    import quotes.reflect.*

    val aTpe = TypeRepr.of[A]

    val constraintTpe = TypeRepr.of[Constraint]

    def rec(tpe: TypeRepr)(using Quotes): Validation[TypeRepr, Expr[String]] =
      tpe.dealias match
        case OrType(left, right) =>
          rec(left)
            .accumulate(rec(right))
            .map((leftResult, rightResult) => '{ $leftResult + " | " + $rightResult })
        case t =>
          val implTpe = constraintTpe.appliedTo(List(aTpe, t))

          Implicits.search(implTpe) match
            case iss: ImplicitSearchSuccess =>
              val implTerm = iss.tree
              Valid(Select.unique(implTerm, "message").asExprOf[String])

            case isf: ImplicitSearchFailure => Invalid(List(t))

    rec(TypeRepr.of[C]) match
      case Valid(value) => '{ "(" + $value + ")" }
      case Invalid(errors) =>
        val missingTypes = errors.map(tpe => s"- ${tpe.show}").mkString("\n")
        compileTimeError(s"""|Missing given instances of Constraint[${aTpe.show}, ...] in union for types:
                             |$missingTypes""".stripMargin)

  /**
   * [[Implication]] for union type.
   * (C1 | C2) ==> C3 only if C1 ==> C3 and C2 ==> C3
   *
   * @tparam C1 the union constraint.
   * @tparam C2 the target constraint.
   * @return the [[Implication]] instance or a compile-time error
   */
  transparent inline def unionImplication[C1, C2]: (C1 ==> C2) = ${ unionImplicationImpl[C1, C2] }

  private def unionImplicationImpl[C1, C2](using Quotes, Type[C1], Type[C2]): Expr[C1 ==> C2] =
    import quotes.reflect.*

    val targetTpe = TypeRepr.of[C2]
    val implicationTpe = TypeRepr.of[Implication]

    def rec(tpe: TypeRepr): Boolean =
      tpe.dealias match
        case OrType(left, right) => rec(left) && rec(right)
        case tpe =>
          val implTpe = implicationTpe.appliedTo(List(tpe, targetTpe))

          Implicits.search(implTpe) match
            case _: ImplicitSearchSuccess => true
            case _: ImplicitSearchFailure => false

    if rec(TypeRepr.of[C1]) then '{ Implication() }
    else report.errorAndAbort("Cannot prove implication")
