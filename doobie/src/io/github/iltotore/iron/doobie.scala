package io.github.iltotore.iron

import cats.Show
import _root_.doobie.util.{Put, Get, Meta}

/**
 * Implicit [[Meta]]s, [[Put]]s  and [[Get]]s for refined types.
 */
object doobie:

  inline given[A, C] (using inline get: Get[A])(using Constraint[A, C], Show[A]): Get[A :| C] =
    get.temap[A :| C](_.refineEither)

  inline given[T](using m: RefinedTypeOps.Mirror[T], ev: Get[m.IronType]): Get[T] =
    ev.asInstanceOf[Get[T]]

  inline given[A, C] (using inline put: Put[A])(using Constraint[A, C], Show[A]): Put[A :| C] =
    put.tcontramap(identity)

  inline given[T](using m: RefinedTypeOps.Mirror[T], ev: Put[m.IronType]): Put[T] =
    ev.asInstanceOf[Put[T]]

  inline given[A, C] (using inline meta: Meta[A])(using Constraint[A, C], Show[A]): Meta[A :| C] =
    meta.tiemap[A :| C](_.refineEither)(identity)

  inline given[T](using m: RefinedTypeOps.Mirror[T], ev: Meta[m.IronType]): Meta[T] =
    ev.asInstanceOf[Meta[T]]
