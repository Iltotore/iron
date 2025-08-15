package io.github.iltotore.iron

import _root_.doobie.{Get, Meta, Put}
import cats.Show

/**
 * Implicit [[Meta]]s, [[Put]]s  and [[Get]]s for refined types.
 */
object doobie extends DoobieLowPriority:

  /**
   * A [[Get]] instance for new types.
   *
   * @param m  the meta information of the refined new type
   * @param ev the value getter of the underlying type
   * @tparam T the new type
   */
  given [T](using m: RefinedType.Mirror[T], ev: Get[m.IronType]): Get[T] =
    ev.asInstanceOf[Get[T]]

  /**
   * A [[Put]] instance for new types.
   *
   * @param m  the meta information of the refined new type
   * @param ev the value setter of the underlying type
   * @tparam T the new type
   */
  given [T](using m: RefinedType.Mirror[T], ev: Put[m.IronType]): Put[T] =
    ev.asInstanceOf[Put[T]]

  /**
   * A [[Meta]] instance for new types.
   *
   * @param m  the meta information of the refined new type
   * @param ev the value getter/setter of the underlying type
   * @tparam T the new type
   */
  given [T](using m: RefinedType.Mirror[T], ev: Meta[m.IronType]): Meta[T] =
    ev.asInstanceOf[Meta[T]]

private trait DoobieLowPriority:

  /**
   * A [[Get]] instance for refined types. Retrieve values using base type's [[Get]] then check the constraint on the
   * retrieved unrefined values.
   *
   * @param get the base type's value getter
   * @tparam A the base type
   * @tparam C the constraint type
   */
  given [A, C](using get: Get[A])(using RuntimeConstraint[A, C], Show[A]): Get[A :| C] =
    get.temap[A :| C](_.refineEither)

  /**
   * A [[Put]] instance for refined types. Retrieve values using base type's [[Put]] then check the constraint on the
   * retrieved unrefined values.
   *
   * @param put the base type's value setter
   * @tparam A the base type
   * @tparam C the constraint type
   */
  given [A, C](using put: Put[A])(using RuntimeConstraint[A, C], Show[A]): Put[A :| C] =
    put.tcontramap(identity)

  /**
   * A [[Meta]] instance for refined types. Retrieve values using base type's [[Meta]] then check the constraint on the
   * retrieved unrefined values.
   *
   * @param meta the base type's value getter/setter
   * @tparam A the base type
   * @tparam C the constraint type
   */
  given [A, C](using meta: Meta[A])(using RuntimeConstraint[A, C], Show[A]): Meta[A :| C] =
    meta.tiemap[A :| C](_.refineEither)(identity)
