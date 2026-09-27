package io.github.iltotore.iron

import io.github.iltotore.iron.refineEither
import io.scalaland.chimney.Transformer
import io.scalaland.chimney.PartialTransformer
import scala.annotation.targetName
import io.scalaland.chimney.partial.Result
import scala.util.NotGiven

object chimney:
  /**
   * Derives [[io.scalaland.chimney.Transformer]] from new type to raw (if [[io.scalaland.chimney.Transformer]] from refined type to raw exists)
   */
  given [From, To](using mirror: RefinedType.Mirror[From], transformer: Transformer[mirror.IronType, To]): Transformer[From, To] =
    transformer.asInstanceOf[Transformer[From, To]]

  /**
   * Derives [[io.scalaland.chimney.Transformer]] from refined types to raw
   */
  given [A, C]: Transformer[A :| C, A] = _.asInstanceOf[A]

  /**
   * Derives [[io.scalaland.chimney.Transformer]] from raw type to new (if [[io.scalaland.chimney.Transformer]] from raw type to refined exists)
   */
  @targetName("FromRefinedTypeToRaw")
  given [To, From](using mirror: RefinedType.Mirror[To], transformer: Transformer[From, mirror.IronType]): Transformer[From, To] =
    transformer.asInstanceOf[Transformer[From, To]]

  /**
   * Derives [[io.scalaland.chimney.Transformer]] from raw type to refined (only with [[Pure]] constraint)
   */
  given [A]: Transformer[A, A :| Pure] = _.asInstanceOf[A :| Pure]

  /**
   * Derives [[io.scalaland.chimney.PartialTransformer]] from raw type to refined
   */
  given [A, C](using constraint: RuntimeConstraint[A, C], ev: NotGiven[Transformer[A, A :| C]]): PartialTransformer[A, A :| C] =
    PartialTransformer(a => Result.fromEitherString[A :| C](a.refineEither))

  /**
   * Derives [[io.scalaland.chimney.PartialTransformer]] from raw type to new
   */
  given [From, To](using
      mirror: RefinedType.Mirror[To],
      transformer: PartialTransformer[From, mirror.IronType],
      ev: NotGiven[Transformer[From, To]]
  ): PartialTransformer[From, To] =
    transformer.asInstanceOf[PartialTransformer[From, To]]
