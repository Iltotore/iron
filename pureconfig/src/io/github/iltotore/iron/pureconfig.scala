package io.github.iltotore.iron

import _root_.pureconfig.ConfigReader
import _root_.pureconfig.error.FailureReason

object pureconfig extends PureConfigLowPrio:
  export RefinedType.Compat.given
private trait PureConfigLowPrio:
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
