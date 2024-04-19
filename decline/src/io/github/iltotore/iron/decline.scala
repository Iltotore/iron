package io.github.iltotore.iron

import _root_.com.monovore.decline.Argument
import _root_.io.github.iltotore.iron.*
import cats.data.Validated
import cats.data.Validated.Valid
import cats.data.Validated.Invalid
import cats.data.ValidatedNel // Add this import

/**
 * Implicit [[Argument]] instances for refined types.
 */
object decline:

  /**
   * An argument reader for refined types. Reads using the underlying type's [[Argument]] then check the constraint.
   *
   * @param argument the argument reader of the underlying type
   * @param constraint the [[Constraint]] implementation to test the decoded value
   * @tparam A the underlying/raw type
   * @tparam B the constraint type
   */
  inline given [A, B](using inline argument: Argument[A], inline constraint: Constraint[A, B]): Argument[A :| B] =
    new Argument[A :| B]:
      def read(string: String): ValidatedNel[String, A :| B] =
        argument.read(string) match
          case Valid(a) => a.refineEither[B] match
              case Left(value)  => Validated.invalidNel(value)
              case Right(value) => Validated.validNel(value)
          case Invalid(e) => Validated.invalid(e)

      def defaultMetavar: String = argument.defaultMetavar

  /**
   * An argument reader for new types. Reads using the underlying refined type's [[Argument]].
   *
   * @param mirror the meta information of the refined new type
   * @param argument the argument reader of the underlying type
   * @tparam T the new type.
   */
  inline given [T](using mirror: RefinedTypeOps.Mirror[T], argument: Argument[mirror.IronType]): Argument[T] =
    argument.asInstanceOf[Argument[T]]
