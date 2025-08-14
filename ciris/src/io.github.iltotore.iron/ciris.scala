package io.github.iltotore.iron

import _root_.ciris.{ConfigDecoder, ConfigError}

object ciris:

  /**
   * A [[ConfigDecoder]] for refined types. Decodes to the underlying type then checks the constraint.
   *
   * @param decoder    the [[ConfigDecoder]] of the underlying type
   * @param constraint the [[RuntimeConstraint]] implementation to test the decoded value
   */
  given [In, A, C](using decoder: ConfigDecoder[In, A], constraint: RuntimeConstraint[A, C]): ConfigDecoder[In, A :| C] =
    decoder.mapEither((_, value) => value.refineEither[C].left.map(ConfigError(_)))

  /**
   * A [[ConfigDecoder]] for new types. Decodes to the underlying type then checks the constraint.
   *
   * @param decoder    the [[ConfigDecoder]] of the underlying type.
   * @param mirror     the mirror of the [[RefinedTypeOps.Mirror]]
   */
  given [In, T](using mirror: RefinedType.Mirror[T], decoder: ConfigDecoder[In, mirror.IronType]): ConfigDecoder[In, T] =
    decoder.asInstanceOf[ConfigDecoder[In, T]]

  /**
   * A [[ConfigDecoder]] for new subtypes. Decodes to the underlying type then checks the constraint.
   *
   * @param decoder    the [[ConfigDecoder]] of the underlying type.
   * @param mirror     the mirror of the [[RefinedTypeOps.Mirror]]
   */
  given [In, T](using mirror: RefinedSubtype.Mirror[T], decoder: ConfigDecoder[In, mirror.IronType]): ConfigDecoder[In, T] =
    decoder.asInstanceOf[ConfigDecoder[In, T]]
