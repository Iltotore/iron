package io.github.iltotore.iron

import _root_.com.monovore.decline.Argument
import _root_.io.github.iltotore.iron.*
import cats.data.Validated
import cats.data.Validated.Valid
import cats.data.Validated.Invalid
import cats.data.ValidatedNel // Add this import

object decline:
  inline given [A, B](using inline argument: Argument[A], inline constraint: Constraint[A, B]): Argument[A :| B] =
    new Argument[A :| B]:
      def read(string: String): ValidatedNel[String, A :| B] =
        argument.read(string) match
          case Valid(a) => a.refineEither[B] match
            case Left(value) => Validated.invalidNel(value)
            case Right(value) => Validated.validNel(value)
          case Invalid(e) => Validated.invalid(e)

      def defaultMetavar: String = argument.defaultMetavar

  inline given [T](using mirror: RefinedTypeOps.Mirror[T], argument: Argument[mirror.IronType]): Argument[T] =
    argument.asInstanceOf[Argument[T]]
