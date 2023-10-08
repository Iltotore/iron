package io.github.iltotore.iron

import scala.compiletime.summonInline
import scala.reflect.TypeTest

/**
 * Utility trait for new types' companion object.
 * 
 * @tparam A the base type of the new type
 * @tparam C the constraint type of the new type
 * @tparam T the new type (equivalent to `A :| C` if `T` is a transparent alias)
 */
trait RefinedTypeOps[A, C, T](using private val _rtc: RuntimeConstraint[A, C]):

  /**
   * The runtime constraint of the underlying [[IronType]]. Can be used in non-inline methods and to improve runtime
   * performances.
   */
  inline def rtc: RuntimeConstraint[A, C] = _rtc

  /**
   * Implicitly refine at compile-time the given value.
   *
   * @param value the value to refine.
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
  inline def assume(value: A): T = value.asInstanceOf[T]

  /**
   * Refine the given value at runtime, resulting in an [[Either]].
   *
   * @return a [[Right]] containing this value as [[T]] or a [[Left]] containing the constraint message.
   * @see [[fromIronType]], [[option]], [[applyUnsafe]].
   */
  def either(value: A): Either[String, T] =
    Either.cond(rtc.test(value), value.asInstanceOf[T], rtc.message)

  /**
   * Refine the given value at runtime, resulting in an [[Option]].
   *
   * @param constraint the constraint to test with the value to refine.
   * @return an Option containing this value as [[T]] or [[None]].
   * @see [[fromIronType]], [[either]], [[applyUnsafe]].
   */
  def option(value: A): Option[T] =
    Option.when(rtc.test(value))(value.asInstanceOf[T])

  /**
   * Refine the given value at runtime.
   *
   * @return this value as [[T]].
   * @throws an [[IllegalArgumentException]] if the constraint is not satisfied.
   * @see [[fromIronType]], [[either]], [[option]].
   */
  inline def applyUnsafe(value: A): T =
    if rtc.test(value) then value.asInstanceOf[T] else throw new IllegalArgumentException(rtc.message)

  def unapply(value: T): Option[A :| C] = Some(value.asInstanceOf[A :| C])

  inline given RefinedTypeOps.Mirror[T] with
    override type BaseType = A
    override type ConstraintType = C

  inline given[R]: TypeTest[T, R] = summonInline[TypeTest[A :| C, R]].asInstanceOf[TypeTest[T, R]]

  given[L] (using test: TypeTest[L, A]): TypeTest[L, T] with
    override def unapply(value: L): Option[value.type & T] = test.unapply(value).filter(rtc.test(_)).asInstanceOf

  extension (wrapper: T)
    inline def value: IronType[A, C] = wrapper.asInstanceOf[IronType[A, C]]


object RefinedTypeOps:

  /**
   * Alias to reduce boilerplate for transparent type aliases.
   *
   * @tparam T the new type which should be a transparent alias for an [[IronType]]
   */
  type Transparent[T] = T match
    case a :| c => RefinedTypeOps[a, c, T]

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