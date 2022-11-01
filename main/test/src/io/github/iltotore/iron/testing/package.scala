package io.github.iltotore.iron

import scala.compiletime.constValue
import io.github.iltotore.iron.compileTime.stringValue

package object testing:

  final class Literal[V]

  inline given [A, V <: Boolean]: Constraint[A, Literal[V]] with

    override inline def test(value: A): Boolean = constValue[V]

    override inline def message: String = stringValue[V]

  extension [A](value: A)

    inline def assertRefine[B](using inline constraint: Constraint[A, B]): Unit = assert(constraint.test(value))
    inline def assertNotRefine[B](using inline constraint: Constraint[A, B]): Unit = assert(!constraint.test(value))
