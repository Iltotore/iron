package io.github.iltotore.iron

import _root_.ciris.{ConfigDecoder, ConfigError}

object ciris extends CirisLowPrio:
  export RefinedType.Compat.compat
private trait CirisLowPrio:

  /**
   * A [[ConfigDecoder]] for refined types. Decodes to the underlying type then checks the constraint.
   *
   * @param decoder    the [[ConfigDecoder]] of the underlying type
   * @param constraint the [[Constraint]] implementation to test the decoded value
   */
  inline given [In, A, C](using inline decoder: ConfigDecoder[In, A], inline constraint: Constraint[A, C]): ConfigDecoder[In, A :| C] =
    decoder.mapEither((_, value) => value.refineEither[C].left.map(ConfigError(_)))
