package io.github.iltotore.iron.macros

import scala.quoted.*

object intersection:

  final class IsIntersection[A]

  // the only instance for IsIntersection used to avoid overhead
  val isIntersectionSingleton: IsIntersection[Any] = new IsIntersection

  transparent inline given [A]: IsIntersection[A] = ${ isIntersectionImpl[A] }

  private def isIntersectionImpl[A](using Quotes, Type[A]): Expr[IsIntersection[A]] =

    import quotes.reflect.*

    val tpe: TypeRepr = TypeRepr.of[A]
    tpe.dealias match
      case i: AndType => ('{ isIntersectionSingleton.asInstanceOf[IsIntersection[A]] }).asExprOf[IsIntersection[A]]
      case other     => report.errorAndAbort(s"${tpe.show} is not a Intersection")

  transparent inline def intersectionCond[A, C](value: A): Boolean = ${ intersectionCondImpl[A, C]('value) }

  private def intersectionCondImpl[A, C](value: Expr[A])(using Quotes, Type[A], Type[C]): Expr[Boolean] =

    import quotes.reflect.*

    val aTpe = TypeRepr.of[A]

    val constraintTpe = TypeRepr.of[io.github.iltotore.iron.Constraint]

    def rec(tpe: TypeRepr): Expr[Boolean] =
      tpe.dealias match
        case AndType(left, right) => '{ ${ rec(left) } && ${ rec(right) } }
        case t =>
          val implTpe = constraintTpe.appliedTo(List(aTpe, t))

          Implicits.search(implTpe) match
            case iss: ImplicitSearchSuccess =>
              val implTerm = iss.tree
              Apply(Select.unique(implTerm, "test"), List(value.asTerm)).asExprOf[Boolean]

            case isf: ImplicitSearchFailure => report.errorAndAbort(s"Could not find implicit ${implTpe}")

    rec(TypeRepr.of[C])
