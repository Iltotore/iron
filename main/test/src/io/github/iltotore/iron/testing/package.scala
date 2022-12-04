package io.github.iltotore.iron
package testing

import scala.compiletime.constValue

import io.github.iltotore.iron.compileTime.stringValue

final class Literal[V]

object Literal:
  inline given [A, V <: Boolean]: Constraint[A, Literal[V]] with

    override inline def test(value: A): Boolean = constValue[V]

    override inline def message: String = stringValue[V]

extension [A](value: A)

  inline def assertRefine[B](using inline constraint: Constraint[A, B]): Unit = assert(constraint.test(value))
  inline def assertNotRefine[B](using inline constraint: Constraint[A, B]): Unit = assert(!constraint.test(value))
