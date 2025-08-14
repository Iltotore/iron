package io.github.iltotore.iron

import scala.compiletime.summonInline
import scala.reflect.TypeTest
import scala.util.boundary
import scala.util.boundary.break

/**
 * Utility trait for new types' companion object.
 *
 * @tparam A the base type of the new type
 * @tparam C the constraint type of the new type
 * @tparam T the new type (equivalent to `A :| C` if `T` is a transparent alias)
 */
private[iron] sealed trait Refined[A, C](using private val _rtc: RuntimeConstraint[A, C]):

  type T

  /**
   * The runtime constraint of the underlying [[IronType]]. Can be used in non-inline methods and to improve runtime
   * performances.
   */
  inline def rtc: RuntimeConstraint[A, C] = _rtc

  /**
   * Implicitly refine at compile-time the given value.
   *
   * @param value the value to refine.
   * @note This method ensures that the value satisfies the constraint. If it doesn't or isn't evaluable at compile-time, the compilation is aborted.
   */
  inline def apply[A1 <: A](inline value: A1)(using inline constraint: Constraint[A, C]): T =
    inline if macros.isIronType[A1, C] then value.asInstanceOf[T]
    else
      macros.assertCondition(value, constraint.test(value), constraint.message)
      value.asInstanceOf[T]

  /**
   * Refine the given value, assuming the constraint holds.
   *
   * @return a constrained value, without performing constraint checks.
   * @see [[assumeAll]], [[apply]], [[applyUnsafe]].
   */
  inline def assume(value: A): T = value.asInstanceOf[T]

  /**
   * Refine the given value at runtime.
   *
   * @return this value as [[T]].
   * @throws an [[IllegalArgumentException]] if the constraint is not satisfied.
   * @see [[either]], [[option]].
   */
  def applyUnsafe(value: A): T =
    if rtc.test(value) then value.asInstanceOf[T] else throw new IllegalArgumentException(rtc.message)

  /**
   * Refine the given value at runtime, resulting in an [[Either]].
   *
   * @return a [[Right]] containing this value as [[T]] or a [[Left]] containing the constraint message.
   * @see [[option]], [[applyUnsafe]].
   */
  def either(value: A): Either[String, T] =
    Either.cond(rtc.test(value), value.asInstanceOf[T], rtc.message)

  /**
   * Refine the given value at runtime, resulting in an [[Option]].
   * @return an Option containing this value as [[T]] or [[None]].
   * @see [[either]], [[applyUnsafe]].
   */
  def option(value: A): Option[T] =
    Option.when(rtc.test(value))(value.asInstanceOf[T])

  /**
   * Refine the given value(s), assuming the constraint holds.
   *
   * @return a wrapper of constrained values, without performing constraint checks.
   * @see [[assume]].
   */
  inline def assumeAll[F[_]](wrapper: F[A]): F[T] = wrapper.asInstanceOf[F[T]]

  /**
   * Refine the given value(s) at runtime.
   *
   * @return the given values as [[T]].
   * @throws IllegalArgumentException if the constraint is not satisfied.
   * @see [[applyUnsafe]].
   */
  inline def applyAllUnsafe[F[_]](wrapper: F[A])(using mapLogic: MapLogic[F]): F[T] =
    mapLogic.map(wrapper, applyUnsafe)

  /**
   * Refine the given value(s) at runtime, resulting in an [[Either]].
   *
   * @return a [[Right]] containing the given values as [[T]] or a [[Left]] containing the constraint message.
   * @see [[either]].
   */
  inline def eitherAll[F[_]](wrapper: F[A])(using mapLogic: MapLogic[F]): Either[String, F[T]] =
    boundary:
      Right(mapLogic.map(
        wrapper,
        either(_) match
          case Right(value) => value
          case Left(error)  => break(Left(error))
      ))

  /**
   * Refine the given value at runtime, resulting in an [[Option]].
   *
   * @return an Option containing the refined values as `F[T]` or [[None]].
   * @see [[option]].
   */
  inline def optionAll[F[_]](wrapper: F[A])(using mapLogic: MapLogic[F]): Option[F[T]] =
    boundary:
      Some(mapLogic.map(
        wrapper,
        option(_) match
          case Some(value) => value
          case None        => break(None)
      ))

  def unapply(value: A): Option[T] = option(value)

  inline given [R]: TypeTest[T, R] = summonInline[TypeTest[A :| C, R]].asInstanceOf[TypeTest[T, R]]

  given [L](using test: TypeTest[L, A]): TypeTest[L, T] with
    override def unapply(value: L): Option[value.type & T] = test.unapply(value).filter(rtc.test(_)).asInstanceOf

  extension (wrapper: T)
    inline def value: IronType[A, C] = wrapper.asInstanceOf[IronType[A, C]]

end Refined

trait RefinedType[A, C](using private val _rtc: RuntimeConstraint[A, C]) extends Refined[A, C]:
  self =>
  override opaque type T = A :| C

  inline given RefinedType.Mirror[T] with
    override type BaseType = A
    override type ConstraintType = C
    override val ops: RefinedType[A, C] = self

end RefinedType

object RefinedType:

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
     * Alias for [[T]].
     */
    type FinalType = T

    /**
     * [[RefinedType]] instance of [[T]].
     */
    def ops: RefinedType[BaseType, ConstraintType]

trait RefinedSubtype[A, C](using private val _rtc: RuntimeConstraint[A, C]) extends Refined[A, C]:
  self =>
  override opaque type T <: A :| C = A :| C

  inline given RefinedSubtype.Mirror[T] with
    override type BaseType = A
    override type ConstraintType = C
    override val ops: RefinedSubtype[A, C] = self

end RefinedSubtype

object RefinedSubtype:

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
     * Alias for [[T]].
     */
    type FinalType = T

    /**
     * [[RefinedSubtype]] instance of [[T]].
     */
    def ops: RefinedSubtype[BaseType, ConstraintType]
