package io.github.iltotore.iron.macros

import io.github.iltotore.iron.internal.*
import io.github.iltotore.iron.internal.Validation.{Valid, Invalid}
import scala.quoted.*

/**
 * Internal macros for intersection types
 */
object intersection:

  /**
   * Typeclass only implemented by intersection types. Used as evidence in implicit methods.
   *
   * @tparam A the underlying intersection type.
   */
  final class IsIntersection[A]

  // Placeholder instance
  val isIntersectionSingleton: IsIntersection[Any] = new IsIntersection

  object IsIntersection:
    transparent inline given [A]: IsIntersection[A] = ${ isIntersectionImpl[A] }

    private def isIntersectionImpl[A](using Quotes, Type[A]): Expr[IsIntersection[A]] =
      import quotes.reflect.*

      val tpe: TypeRepr = TypeRepr.of[A]
      tpe.dealias match
        case i: AndType => ('{ isIntersectionSingleton.asInstanceOf[IsIntersection[A]] }).asExprOf[IsIntersection[A]]
        case other      => report.errorAndAbort(s"${tpe.show} is not a Intersection")

  transparent inline def intersectionCond[A, C](value: A): Boolean = ${ intersectionCondImpl[A, C]('value) }

  private def intersectionCondImpl[A, C](value: Expr[A])(using Quotes, Type[A], Type[C]): Expr[Boolean] =

    import quotes.reflect.*

    val aTpe = TypeRepr.of[A]

    val constraintTpe = TypeRepr.of[io.github.iltotore.iron.Constraint]

    def rec(tpe: TypeRepr): Validation[TypeRepr, Expr[Boolean]] =
      tpe.dealias match
        case AndType(left, right) =>
          rec(left)
            .accumulate(rec(right))
            .map((leftResult, rightResult) => '{ $leftResult && $rightResult })
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
        compileTimeError(s"""|Missing given instances of Constraint[${aTpe.show}, ...] in intersection for types:
                             |$missingTypes""".stripMargin)

  /**
   * Constraint message for intersection type.
   *
   * @tparam A the input type (like in `Constraint[A, C]`).
   * @tparam C the constraint type (like in `Constraint[A, C])`. Should be an intersection.
   * @return the generated message for this constraint intersection.
   */
  inline def intersectionMessage[A, C]: String = ${ intersectionMessageImpl[A, C] }

  private def intersectionMessageImpl[A, C](using Quotes, Type[A], Type[C]): Expr[String] =
    import quotes.reflect.*

    val aTpe = TypeRepr.of[A]

    val constraintTpe = TypeRepr.of[io.github.iltotore.iron.Constraint]

    def rec(tpe: TypeRepr)(using Quotes): Validation[TypeRepr, Expr[String]] =
      tpe.dealias match
        case AndType(left, right) =>
          rec(left)
            .accumulate(rec(right))
            .map((leftResult, rightResult) => '{ $leftResult + " & " + $rightResult })
        case t =>
          val implTpe = constraintTpe.appliedTo(List(aTpe, t))

          Implicits.search(implTpe) match
            case iss: ImplicitSearchSuccess =>
              val implTerm = iss.tree
              Valid(Select.unique(implTerm, "message").asExprOf[String])

            case isf: ImplicitSearchFailure => Invalid(List(t))

    rec(TypeRepr.of[C]) match
      case Valid(value) => value
      case Invalid(errors) =>
        val missingTypes = errors.map(tpe => s"- ${tpe.show}").mkString("\n")
        compileTimeError(s"""|Missing given instances of Constraint[${aTpe.show}, ...] in intersection for types:
                             |$missingTypes""".stripMargin)
