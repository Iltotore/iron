package io.github.iltotore.iron

type RefinedTypeOps[T] = T match
  case IronType[a, c] => RefinedTypeOpsImpl[a, c, T]

class RefinedTypeOpsImpl[A, C, T]:
  inline def apply(value: A)(using Constraint[A, C]): T =
    autoRefine[A, C](value).asInstanceOf[T]

  inline def applyUnsafe(value: A)(using Constraint[A, C]): T =
    value.refine[C].asInstanceOf[T]

extension [A, C, T](ops: RefinedTypeOpsImpl[A, C, T])
  inline def either(value: A)(using constraint: Constraint[A, C]): Either[String, T] =
    Either.cond(constraint.test(value), value.asInstanceOf[T], constraint.message)

  inline def option(value: A)(using constraint: Constraint[A, C]): Option[T] =
    Option.when(constraint.test(value))(value.asInstanceOf[T])
