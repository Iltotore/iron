package io.github.iltotore.iron
package testing

import scala.compiletime.constValue

extension [A](value: A)

  inline def assertRefine[B](using inline constraint: Constraint[A, B]): Unit = assert(constraint.test(value))
  inline def assertNotRefine[B](using inline constraint: Constraint[A, B]): Unit = assert(!constraint.test(value))

inline def assertGiven[T](using inline ev: T = null): Unit = assert(ev != null)