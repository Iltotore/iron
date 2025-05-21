package io.github.iltotore.iron

import cats.Show
import _root_.doobie.{Get, Meta, Put}
import io.github.iltotore.iron.internal.NotNothing

/**
 * Implicit [[Meta]]s, [[Put]]s  and [[Get]]s for refined types.
 */
object doobie:

  /**
   * A [[Get]] instance for refined types. Retrieve values using base type's [[Get]] then check the constraint on the
   * retrieved unrefined values.
   *
   * @param get the base type's value getter
   * @tparam A the base type
   * @tparam C the constraint type
   */
  inline given [A, C](using inline get: Get[A], inline notNothing: NotNothing[A])(using Constraint[A, C], Show[A]): Get[A :| C] =
    get.temap[A :| C](_.refineEither)

  /**
   * A [[Get]] instance for new types.
   *
   * @param m the meta information of the refined new type
   * @param ev the value getter of the underlying type
   * @tparam T the new type
   */
  inline given [T](using m: RefinedType.Mirror[T], ev: Get[m.IronType]): Get[T] =
    ev.asInstanceOf[Get[T]]

  /**
   * A [[Put]] instance for refined types. Retrieve values using base type's [[Put]] then check the constraint on the
   * retrieved unrefined values.
   *
   * @param put the base type's value setter
   * @tparam A the base type
   * @tparam C the constraint type
   */
  inline given [A, C](using inline put: Put[A], inline notNothing: NotNothing[A])(using Constraint[A, C], Show[A]): Put[A :| C] =
    put.tcontramap(identity)

  /**
   * A [[Put]] instance for new types.
   *
   * @param m the meta information of the refined new type
   * @param ev the value setter of the underlying type
   * @tparam T the new type
   */
  inline given [T](using m: RefinedType.Mirror[T], ev: Put[m.IronType]): Put[T] =
    ev.asInstanceOf[Put[T]]

  /**
   * A [[Meta]] instance for refined types. Retrieve values using base type's [[Meta]] then check the constraint on the
   * retrieved unrefined values.
   *
   * @param meta the base type's value getter/setter
   * @tparam A the base type
   * @tparam C the constraint type
   */
  inline given [A, C](using inline meta: Meta[A], inline notNothing: NotNothing[A])(using Constraint[A, C], Show[A]): Meta[A :| C] =
    meta.tiemap[A :| C](_.refineEither)(identity)

  /**
   * A [[Meta]] instance for new types.
   *
   * @param m  the meta information of the refined new type
   * @param ev the value getter/setter of the underlying type
   * @tparam T the new type
   */
  inline given [T](using m: RefinedType.Mirror[T], ev: Meta[m.IronType]): Meta[T] =
    ev.asInstanceOf[Meta[T]]
