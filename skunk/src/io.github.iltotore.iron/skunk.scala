package io.github.iltotore.iron

import _root_.skunk.*

object skunk:

  /**
   * Explicit conversion for refining a [[Codec]]. Decodes to the underlying type then checks the constraint.
   *
   * @param constraint the [[Constraint]] implementation to test the decoded value
   */
  extension [A](codec: Codec[A])
    def refined[C](using constraint: RuntimeConstraint[A, C]): Codec[A :| C] =
      codec.eimap[A :| C](_.refineEither[C])(_.asInstanceOf[A])

  /**
   * A [[Codec]] for refined types. Decodes to the underlying type then checks the constraint.
   *
   * @param codec      the [[Codec]] of the underlying type
   * @param constraint the [[Constraint]] implementation to test the decoded value
   */
  given [A, C](using codec: Codec[A], constraint: RuntimeConstraint[A, C]): Codec[A :| C] =
    codec.refined

  /**
   * A [[Codec]] for new types.
   */
  inline given [T](using mirror: RefinedType.Mirror[T], ev: Codec[mirror.IronType]): Codec[T] =
    ev.asInstanceOf[Codec[T]]

  /**
   * A [[Codec]] for new subtypes.
   */
  inline given [T](using mirror: RefinedSubtype.Mirror[T], ev: Codec[mirror.IronType]): Codec[T] =
    ev.asInstanceOf[Codec[T]]
