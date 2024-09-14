package io.github.iltotore.iron

import io.github.iltotore.iron.macros.union.*
import io.github.iltotore.iron.macros.intersection.*

/**
 * A typeclass representing the implementation of a constraint of a certain type of value.
 *
 * @tparam A the type of the value to test.
 * @tparam C the constraint associated with this implementation.
 */
trait Constraint[A, C]:

  inline def test(inline value: A): Boolean

  inline def message: String

object Constraint:
  class UnionConstraint[A, C] extends Constraint[A, C]:

    override inline def test(inline value: A): Boolean = unionCond[A, C](value)

    override inline def message: String = unionMessage[A, C]

  inline given [A, C](using inline u: IsUnion[C]): UnionConstraint[A, C] = new UnionConstraint

  class IntersectionConstraint[A, C] extends Constraint[A, C]:

    override inline def test(inline value: A): Boolean = intersectionCond[A, C](value)

    override inline def message: String = intersectionMessage[A, C]

  inline given [A, C](using inline i: IsIntersection[C]): IntersectionConstraint[A, C] = new IntersectionConstraint
