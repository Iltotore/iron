package io.github.iltotore.iron

import scala.compiletime.summonInline
import scala.reflect.TypeTest

type RefinedTypeOps[T] = T match
  case IronType[a, c] => RefinedTypeOpsImpl[a, c, T]

object RefinedTypeOps:

  /**
   * Typelevel access to a "new type"'s informations. It is similar to [[scala.deriving.Mirror]].
   * @tparam T the new type (usually a type alias).
   */
  trait Mirror[T]:

    /**
     * The base type of the mirrored type without any constraint.
     */
    type BaseType

    /**
     * The constraint of the mirrored type.
     */
    type ConstraintType

    /**
     * Alias for `BaseType :| ConstraintType`
     */
    type IronType = BaseType :| ConstraintType

    /**
     * Alias for [[T]]. Also equivalent to [[IronType]] if the type alias of the mirrored new type is transparent.
     *
     * {{{
     * type Temperature = Double :| Positive
     * object Temperature extends RefinedTypeOps[Temperature]
     *
     * //FinalType =:= IronType
     * }}}
     *
     * {{{
     * opaque type Temperature = Double :| Positive
     * object Temperature extends RefinedTypeOps[Temperature]
     *
     * //FinalType =/= IronType
     * }}}
     */
    type FinalType = T

trait RefinedTypeOpsImpl[A, C, T]:
  /**
   * Implicitly refine at compile-time the given value.
   *
   * @param value      the value to refine.
   * @param constraint the implementation of `C` to check.
   * @tparam A the refined type.
   * @tparam C the constraint applied to the type.
   * @return the given value typed as [[IronType]]
   * @note This method ensures that the value satisfies the constraint. If it doesn't or isn't evaluable at compile-time, the compilation is aborted.
   */
  inline def apply(value: A :| C): T = value.asInstanceOf[T]

  /**
   * Refine the given value at runtime, assuming the constraint holds.
   *
   * @return a constrained value, without performing constraint checks.
   * @see [[apply]], [[applyUnsafe]].
   */
  inline def assume(value: A): T = value.assume[C].asInstanceOf[T]

  /**
   * Refine the given value at runtime, resulting in an [[Either]].
   *
   * @param constraint the constraint to test with the value to refine.
   * @return a [[Right]] containing this value as [[T]] or a [[Left]] containing the constraint message.
   * @see [[fromIronType]], [[option]], [[applyUnsafe]].
   */
  inline def either(value: A)(using constraint: Constraint[A, C]): Either[String, T] =
    Either.cond(constraint.test(value), value.asInstanceOf[T], constraint.message)

  /**
   * Refine the given value at runtime, resulting in an [[Option]].
   *
   * @param constraint the constraint to test with the value to refine.
   * @return an Option containing this value as [[T]] or [[None]].
   * @see [[fromIronType]], [[either]], [[applyUnsafe]].
   */
  inline def option(value: A)(using constraint: Constraint[A, C]): Option[T] =
    Option.when(constraint.test(value))(value.asInstanceOf[T])

  /**
   * Refine the given value at runtime.
   *
   * @param constraint the constraint to test with the value to refine.
   * @return this value as [[T]].
   * @throws an [[IllegalArgumentException]] if the constraint is not satisfied.
   * @see [[fromIronType]], [[either]], [[option]].
   */
  inline def applyUnsafe(value: A)(using Constraint[A, C]): T =
    value.refine[C].asInstanceOf[T]

  inline def unapply(value: T): Option[A :| C] = Some(value.asInstanceOf[A :| C])

  inline given RefinedTypeOps.Mirror[T] with
    override type BaseType = A
    override type ConstraintType = C

  inline given [R]: TypeTest[T, R] = summonInline[TypeTest[A :| C, R]].asInstanceOf[TypeTest[T, R]]

  inline given [L](using inline constraint: Constraint[A, C]): TypeTest[L, T] =
    val test = summonInline[TypeTest[L, A]]

    new TypeTest:
      override def unapply(value: L): Option[value.type & T] =
        test.unapply(value).filter(constraint.test(_)).asInstanceOf

  extension (wrapper: T)
    inline def value: IronType[A, C] = wrapper.asInstanceOf[IronType[A, C]]