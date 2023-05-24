package io.github.iltotore.iron

type RefinedTypeOps[T] = T match
  case IronType[a, c] => RefinedTypeOpsImpl[a, c, T]

class RefinedTypeOpsImpl[A, C, T]:
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
  inline def apply(value: A)(using Constraint[A, C]): T =
    autoRefine[A, C](value).asInstanceOf[T]

  /**
   * Refine the given value at runtime, assuming the constraint holds.
   *
   * @return a constrained value, without performing constraint checks.
   * @see [[apply]], [[applyUnsafe]].
   */
  inline def assume(value: A): T = value.assume[C].asInstanceOf[T]

  extension (wrapper: T)
    inline def value: IronType[A, C] = wrapper.asInstanceOf[IronType[A, C]]
    
extension [A, C, T](ops: RefinedTypeOpsImpl[A, C, T])

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
   * Refine the given value at runtime, resulting in an [[Option]].
   *
   * @param implication the constraint (with possible implication) to test with the value to refine.
   * @return an Option containing this value as [[T]] or [[None]].
   * @see [[fromIronType]], [[either]], [[applyUnsafe]].
   */
  inline def fromIronType[C1](value: IronType[A, C1])(using Implication[C1, C]): T =
    value.asInstanceOf[T]

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
