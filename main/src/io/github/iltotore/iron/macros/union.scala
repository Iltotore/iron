package io.github.iltotore.iron.macros

import scala.quoted.*

object union:

  final class IsUnion[A]

  // the only instance for IsUnion used to avoid overhead
  val isUnionSingleton: IsUnion[Any] = new IsUnion

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

    val constraintTpe = TypeRepr.of[io.github.iltotore.iron.Constraint]

    def rec(tpe: TypeRepr): Expr[Boolean] =
      tpe.dealias match
        case OrType(left, right) => '{ ${ rec(left) } || ${ rec(right) } }
        case t =>
          val implTpe = constraintTpe.appliedTo(List(aTpe, t))

          Implicits.search(implTpe) match
            case iss: ImplicitSearchSuccess =>
              val implTerm = iss.tree
              Apply(Select.unique(implTerm, "test"), List(value.asTerm)).asExprOf[Boolean]

            case isf: ImplicitSearchFailure => report.errorAndAbort(s"Could not find implicit ${implTpe}")

    rec(TypeRepr.of[C])
