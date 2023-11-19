package io.github.iltotore.iron

import _root_.skunk.*

object skunk:

  /**
   * Explicit conversion for refining a [[Codec]]. Decodes to the underlying type then checks the constraint.
   *
   * @param constraint the [[Constraint]] implementation to test the decoded value
   */
  extension [A](codec: Codec[A])
    inline def refined[C](using inline constraint: Constraint[A, C]): Codec[A :| C] =
      codec.eimap[A :| C](_.refineEither[C])(_.asInstanceOf[A])

  /**
   * A [[Codec]] for refined types. Decodes to the underlying type then checks the constraint.
   *
   * @param codec      the [[Codec]] of the underlying type
   * @param constraint the [[Constraint]] implementation to test the decoded value
   */
  inline given [A, C](using inline codec: Codec[A], inline constraint: Constraint[A, C]): Codec[A :| C] = codec.refined
