package io.github.iltotore.iron

import _root_.zio.blocks.schema.Validation

/**
 * A lossless translation, when one exists, from an Iron constraint to ZIO
 * Blocks' validation AST.
 *
 * The built-in instance recognizes the subset of Iron constraints for which
 * ZIO Blocks has identical semantics. Applications can define a more specific
 * given for their own constraints.
 */
trait ZioBlocksValidation[A, C]:

  def validation: Option[Validation[A]]

object ZioBlocksValidation:

  inline given derived[A, C]: ZioBlocksValidation[A, C] = ${ ZioBlocksValidationMacros.derived[A, C] }
