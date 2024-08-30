package io.github.iltotore.iron

import _root_.pureconfig.ConfigReader
import _root_.pureconfig.error.FailureReason

object pureconfig:
  final case class RefinedConfigError(description: String) extends FailureReason

  /**
   * A [[ConfigReader]] for refined types. Decodes to the underlying type then checks the constraint.
   *
   * @param reader     the [[ConfigReader]] of the underlying type
   * @param constraint the [[Constraint]] implementation to test the decoded value
   */
  inline given [A, C](using inline reader: ConfigReader[A], inline constraint: Constraint[A, C]): ConfigReader[A :| C] =
    reader
      .emap: value =>
        value
          .refineEither[C]
          .left
          .map(RefinedConfigError(_))

  /**
   * A [[ConfigReader]] for new types. Decodes to the underlying type then checks the constraint

   * @param mirror the mirror of the [[RefinedTypeOps.Mirror]]
   * @param reader the [[ConfigReader]] of the underlying type
   */
  inline given [A](using mirror: RefinedTypeOps.Mirror[A], reader: ConfigReader[mirror.IronType]): ConfigReader[A] =
    reader.asInstanceOf[ConfigReader[A]]
