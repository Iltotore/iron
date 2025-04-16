package io.github.iltotore.iron

import cats.Show
import _root_.doobie.{Put, Get, Meta}

/**
 * Implicit [[Meta]]s, [[Put]]s  and [[Get]]s for refined types.
 */
object doobie extends DoobieLowPrio:
  export RefinedType.Compat.given
private trait DoobieLowPrio:

  /**
   * A [[Get]] instance for refined types. Retrieve values using base type's [[Get]] then check the constraint on the
   * retrieved unrefined values.
   *
   * @param get the base type's value getter
   * @tparam A the base type
   * @tparam C the constraint type
   */
  inline given [A, C](using inline get: Get[A])(using Constraint[A, C], Show[A]): Get[A :| C] =
    get.temap[A :| C](_.refineEither)

  /**
   * A [[Put]] instance for refined types. Retrieve values using base type's [[Put]] then check the constraint on the
   * retrieved unrefined values.
   *
   * @param put the base type's value setter
   * @tparam A the base type
   * @tparam C the constraint type
   */
  inline given [A, C](using inline put: Put[A])(using Constraint[A, C], Show[A]): Put[A :| C] =
    put.tcontramap(identity)

  /**
   * A [[Meta]] instance for refined types. Retrieve values using base type's [[Meta]] then check the constraint on the
   * retrieved unrefined values.
   *
   * @param meta the base type's value getter/setter
   * @tparam A the base type
   * @tparam C the constraint type
   */
  inline given [A, C](using inline meta: Meta[A])(using Constraint[A, C], Show[A]): Meta[A :| C] =
    meta.tiemap[A :| C](_.refineEither)(identity)
