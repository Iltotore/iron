package io.github.iltotore.iron.constraint

import io.github.iltotore.iron.{==>, Constraint, Implication}
import io.github.iltotore.iron.compileTime.*

import scala.compiletime.{constValue, summonInline}
import scala.compiletime.ops.any.ToString
import scala.compiletime.ops.boolean
import scala.util.NotGiven

/**
 * Constraints working for any type (e.g [[any.StrictEqual]]) and constraint operators (e.g [[any.Not]]...).
 */
object any:

  /**
   * An always-valid constraint.
   */
  final class True

  /**
   * Alias for [[True]]. Usually used for new types with no constraint.
   * @see "Creating new types" reference.
   */
  type Pure = True

  /**
   * An always-invalid constraint.
   */
  final class False

  /**
   * A constraint decorator with a custom description.
   *
   * @tparam C the decorated constraint.
   * @tparam V the description to attach.
   * @example {{{
   * //Literal
   * type PosInt = Greater[0] DescribedAs "Should be positive"
   *
   * //Using type-level String concatenation (example taken from `numeric`)
   * import io.github.iltotore.iron.ops.*
   *
   * type GreaterEqual[V] = (Greater[V] || StrictEqual[V]) DescribedAs ("Should be greater than or equal to " + V)
   * }}}
   */
  final class DescribedAs[C, V <: String]

  /**
   * A constraint decorator acting like a boolean "not".
   * @tparam C the decorated constraint.
   */
  final class Not[C]

  /**
   * Alias for [[Not]].
   */
  type ![C] = C match
    case Boolean => boolean.![C]
    case _       => Not[C]

  /**
   * A constraint operator acting like a boolean "xor".
   * @tparam C1 the left constraint.
   * @tparam C2 the right constraint.
   */
  final class Xor[C1, C2]

  /**
   * Tests strict equality with the given value.
   *
   * @tparam V the value the input must be equal to.
   */
  final class StrictEqual[V]

  /**
   * Tests strict equality with any one of the given values.
   * @tparam V the values the input must be in.
   * @example {{{
   * type Digit = In[(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)]
   * }}}
   */
  type In[V <: NonEmptyTuple] = V match
    case *:[h, EmptyTuple] => StrictEqual[h]
    case *:[h, t]          => StrictEqual[h] | In[t]

  object True:

    inline given [A]: Constraint[A, True] with

      override inline def test(value: A): Boolean = true

      override inline def message: String = "Always valid"

    /**
     * True implies Not[False]
     */
    given (True ==> Not[False]) = Implication()

  object False:

    inline given [A]: Constraint[A, False] with

      override inline def test(value: A): Boolean = false

      override inline def message: String = "Always invalid"

    /**
     * False implies Not[True]
     */
    given (False ==> Not[True]) = Implication()

  object DescribedAs:
    class DescribedAsConstraint[A, C, Impl <: Constraint[A, C], V <: String](using Impl) extends Constraint[A, DescribedAs[C, V]]:

      override inline def test(value: A): Boolean = summonInline[Impl].test(value)

      override inline def message: String = constValue[V]

    inline given [A, C, Impl <: Constraint[A, C], V <: String](using inline constraint: Impl): DescribedAsConstraint[A, C, Impl, V] =
      new DescribedAsConstraint

    /**
     * A described constraint C1 implies C1.
     */
    given [C1, C2, V <: String](using C1 ==> C2): ((C1 DescribedAs V) ==> C2) = Implication()

    /**
     * A constraint C1 implies its "described" form.
     */
    given [C1, C2, V <: String](using C1 ==> C2): (C1 ==> (C2 DescribedAs V)) = Implication()

  object Not:
    class NotConstraint[A, C, Impl <: Constraint[A, C]](using Impl) extends Constraint[A, Not[C]]:

      override inline def test(value: A): Boolean =
        !summonInline[Impl].test(value)

      override inline def message: String =
        "!(" + summonInline[Impl].message + ")"

    inline given [A, C, Impl <: Constraint[A, C]](using inline constraint: Impl): NotConstraint[A, C, Impl] = new NotConstraint

    /**
     * Doubly inverted C implies C.
     */
    given [C1, C2](using C1 ==> C2): (Not[Not[C1]] ==> C2) = Implication()

    /**
     * C implies doubly inverted C.
     */
    given [C1, C2](using C1 ==> C2): (C1 ==> Not[Not[C2]]) = Implication()

    given [C1, C2](using C1 ==> C2): (Not[C1] ==> Not[C2]) = Implication()

  object Xor:

    class XorConstraint[A, C1, C2, Impl1 <: Constraint[A, C1], Impl2 <: Constraint[A, C2]] extends Constraint[A, Xor[C1, C2]]:

      override inline def test(value: A): Boolean = summonInline[Impl1].test(value) != summonInline[Impl2].test(value)

      override inline def message: String = "(" + summonInline[Impl1].message + " xor " + summonInline[Impl2].message + ")"

    inline given [A, C1, C2, Impl1 <: Constraint[A, C1], Impl2 <: Constraint[A, C2]](using
        inline impl1: Impl1,
        impl2: Impl2
    ): XorConstraint[A, C1, C2, Impl1, Impl2] =
      new XorConstraint

    /**
     * Xor[C1, C2] <=> Xor[C2, C1].
     */
    given [C1, C2]: (Xor[C1, C2] ==> Xor[C2, C1]) = Implication()

    /**
     * C1 implies Xor[C1, C2] if C1 and C2 are exclusives.
     */
    given left[C1, C2, C3](using C1 ==> Not[C2], C1 ==> C3): (C1 ==> Xor[C3, C2]) = Implication()

    /**
     * C1 implies Xor[C2, C1] if C1 and C2 are exclusives.
     */
    given right[C1, C2, C3](using C1 ==> Not[C2], C1 ==> C3): (C1 ==> Xor[C2, C3]) = Implication()

  object StrictEqual:

    private trait StrictEqualConstraint[A, V] extends Constraint[A, StrictEqual[V]]:
      override inline def message: String = "Should strictly equal to " + stringValue[V]

    inline given [A, V]: StrictEqualConstraint[A, V] with
      override inline def test(value: A): Boolean = value == constValue[V]

    inline given bigDecimalDouble[V <: Float | Double]: StrictEqualConstraint[BigDecimal, V] with
      override inline def test(value: BigDecimal): Boolean = value == BigDecimal(doubleValue[V])

    inline given bigDecimalLong[V <: Int | Long]: StrictEqualConstraint[BigDecimal, V] with
      override inline def test(value: BigDecimal): Boolean = value == BigDecimal(longValue[V])

    inline given [V <: Int | Long]: StrictEqualConstraint[BigInt, V] with
      override inline def test(value: BigInt): Boolean = value == BigInt(longValue[V])

    inline given jBigDecimalDouble[V <: Float | Double]: StrictEqualConstraint[java.math.BigDecimal, V] with
      override inline def test(value: java.math.BigDecimal): Boolean =
        value == java.math.BigDecimal.valueOf(doubleValue[V])

    inline given jBigDecimalLong[V <: Int | Long]: StrictEqualConstraint[java.math.BigDecimal, V] with
      override inline def test(value: java.math.BigDecimal): Boolean =
        value == java.math.BigDecimal.valueOf(longValue[V])

    inline given jBigInteger[V <: Int | Long]: StrictEqualConstraint[java.math.BigInteger, V] with
      override inline def test(value: java.math.BigInteger): Boolean =
        value == java.math.BigInteger.valueOf(longValue[V])
