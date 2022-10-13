package io.github.iltotore.iron

import cats.data.{Validated, ValidatedNec, ValidatedNel}

package object catsSupport:

  extension [A](value: A)

    inline def refineValidated[B](using inline constraint: Constraint[A, B]): Validated[String, A :| B] =
      Validated.cond(constraint.test(value), value.asInstanceOf, constraint.message)

    inline def refineNec[B](using inline constraint: Constraint[A, B]): ValidatedNec[String, A :| B] =
      Validated.condNec(constraint.test(value), value.asInstanceOf[A :| B], constraint.message)

    inline def refineNel[B](using inline constraint: Constraint[A, B]): ValidatedNel[String, A :| B] =
      Validated.condNel(constraint.test(value), value.asInstanceOf[A :| B], constraint.message)
