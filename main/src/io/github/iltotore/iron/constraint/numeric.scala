package io.github.iltotore.iron.constraint

import io.github.iltotore.iron.constraint.any.*
import io.github.iltotore.iron.compileTime.*
import io.github.iltotore.iron.{==>, Constraint, Implication}

import scala.util.NotGiven

/**
 * Number-related constraints.
 */
object numeric:

  /**
   * Tests strict superiority.
   *
   * @tparam V the value the input must be greater than.
   */
  final class Greater[V]

  /**
   * Tests strict inferiority.
   *
   * @tparam V the value the input must be less than.
   */
  final class Less[V]

  /**
   * Tests non-strict superiority.
   *
   * @tparam V the value the input must be greater than or equal to.
   */
  type GreaterEqual[V] = (Greater[V] | StrictEqual[V]) DescribedAs ("Should be greater than or equal to " + V)

  /**
   * Tests non-strict inferiority.
   *
   * @tparam V the value the input must be less than or equal to.
   */
  type LessEqual[V] = (Less[V] | StrictEqual[V]) DescribedAs ("Should be less than or equal to " + V)

  /**
   * Tests if the input is strictly positive.
   */
  type Positive = Greater[0] DescribedAs "Should be strictly positive"

  /**
   * Tests if the input is strictly negative.
   */
  type Negative = Less[0] DescribedAs "Should be strictly negative"

  object Interval:

    /**
     * Tests if the input is included in `(V1, V2)`
     *
     * @tparam V1 the lower bound, exclusive.
     * @tparam V2 the upper bound, exclusive.
     */
    type Open[V1, V2] = (Greater[V1] & Less[V2]) DescribedAs ("Should be included in (" + V1 + ", " + V2 + ")")

    /**
     * Tests if the input is included in `(V1, V2]`
     *
     * @tparam V1 the lower bound, exclusive.
     * @tparam V2 the upper bound, inclusive.
     */
    type OpenClosed[V1, V2] = (Greater[V1] & LessEqual[V2]) DescribedAs ("Should be included in (" + V1 + ", " + V2 + "]")

    /**
     * Tests if the input is included in `[V1, V2)`
     *
     * @tparam V1 the lower bound, inclusive.
     * @tparam V2 the upper bound, exclusive.
     */
    type ClosedOpen[V1, V2] = (GreaterEqual[V1] & Less[V2]) DescribedAs ("Should be included in [" + V1 + ", " + V2 + ")")

    /**
     * Tests if the input is included in `[V1, V2]`
     *
     * @tparam V1 the lower bound, inclusive.
     * @tparam V2 the upper bound, inclusive.
     */
    type Closed[V1, V2] = (GreaterEqual[V1] & LessEqual[V2]) DescribedAs ("Should be included in [" + V1 + ", " + V2 + "]")

  /**
   * Tests if the input is a multiple of V.
   *
   * @tparam V the expected divisor of the given input.
   * @see [[Divide]]
   */
  final class Multiple[V]

  /**
   * Tests if the input is a divisor of V.
   *
   * @tparam V the expected multiple of the given input.
   * @see [[Multiple]]
   */
  final class Divide[V]

  /**
   * Tests if the input is even (a multiple of 2).
   */
  type Even = Multiple[2]

  /**
   * Tests if the input is odd (not a multiple of 2).
   */
  type Odd = Not[Even]

  /**
   * Tests if the input is not a representable number.
   */
  final class NaN

  /**
   * Tests if the input is whether `+infinity` or `-infinity`.
   */
  final class Infinity

  object Greater:
    private trait GreaterConstraint[A, V <: NumConstant] extends Constraint[A, Greater[V]]:
      override inline def message: String = "Should be greater than " + stringValue[V]

    inline given [V <: NumConstant]: GreaterConstraint[Int, V] with
      override inline def test(value: Int): Boolean = value > doubleValue[V]

    inline given [V <: NumConstant]: GreaterConstraint[Long, V] with
      override inline def test(value: Long): Boolean = value > doubleValue[V]

    inline given [V <: NumConstant]: GreaterConstraint[Float, V] with
      override inline def test(value: Float): Boolean = value > doubleValue[V]

    inline given [V <: NumConstant]: GreaterConstraint[Double, V] with
      override inline def test(value: Double): Boolean = value > doubleValue[V]

    inline given bigDecimalDouble[V <: NumConstant]: GreaterConstraint[BigDecimal, V] with
      override inline def test(value: BigDecimal): Boolean = value > BigDecimal(doubleValue[V])

    inline given bigDecimalLong[V <: Int | Long]: GreaterConstraint[BigDecimal, V] with
      override inline def test(value: BigDecimal): Boolean = value > BigDecimal(longValue[V])

    inline given [V <: Int | Long]: GreaterConstraint[BigInt, V] with
      override inline def test(value: BigInt): Boolean = value > BigInt(longValue[V])

    given [V1, V2](using V1 > V2 =:= true): (Greater[V1] ==> Greater[V2]) = Implication()

    given [V1, V2](using V1 > V2 =:= true): (StrictEqual[V1] ==> Greater[V2]) = Implication()

    given notLess[V1, V2](using V1 >= V2 =:= true): (Greater[V1] ==> Not[Less[V2]]) = Implication()

    given notEq[V1, V2](using V1 >= V2 =:= true): (Greater[V1] ==> Not[StrictEqual[V2]]) = Implication()

    given [V1, V2](using V1 >= V2 =:= true): (StrictEqual[V2] ==> Not[Greater[V1]]) = Implication()

  object Less:
    private trait LessConstraint[A, V <: NumConstant] extends Constraint[A, Less[V]]:
      override inline def message: String = "Should be less than " + stringValue[V]

    inline given [V <: NumConstant]: LessConstraint[Int, V] with
      override inline def test(value: Int): Boolean = value < doubleValue[V]

    inline given [V <: NumConstant]: LessConstraint[Long, V] with
      override inline def test(value: Long): Boolean = value < doubleValue[V]

    inline given [V <: NumConstant]: LessConstraint[Float, V] with
      override inline def test(value: Float): Boolean = value < doubleValue[V]

    inline given [V <: NumConstant]: LessConstraint[Double, V] with
      override inline def test(value: Double): Boolean = value < doubleValue[V]

    inline given bigDecimalDouble[V <: NumConstant]: LessConstraint[BigDecimal, V] with
      override inline def test(value: BigDecimal): Boolean = value < BigDecimal(doubleValue[V])

    inline given bigDecimalLong[V <: Int | Long]: LessConstraint[BigDecimal, V] with
      override inline def test(value: BigDecimal): Boolean = value < BigDecimal(longValue[V])

    inline given [V <: Int | Long]: LessConstraint[BigInt, V] with
      override inline def test(value: BigInt): Boolean = value < BigInt(longValue[V])

    given [V1, V2](using V1 < V2 =:= true): (Less[V1] ==> Less[V2]) = Implication()

    given [V1, V2](using V1 < V2 =:= true): (StrictEqual[V1] ==> Less[V2]) = Implication()

    given notGreater[V1, V2](using V1 <= V2 =:= true): (Less[V1] ==> Not[Greater[V2]]) = Implication()

    given notEq[V1, V2](using V1 <= V2 =:= true): (Less[V1] ==> Not[StrictEqual[V2]]) = Implication()

    given [V1, V2](using V1 <= V2 =:= true): (StrictEqual[V2] ==> Not[Less[V1]]) = Implication()

  object Multiple:
    private trait MultipleConstraint[A, V <: NumConstant] extends Constraint[A, Multiple[V]]:
      override inline def message: String = "Should be a multiple of " + stringValue[V]

    inline given [V <: NumConstant]: MultipleConstraint[Int, V] with
      override inline def test(value: Int): Boolean = value % doubleValue[V] == 0

    inline given [V <: NumConstant]: MultipleConstraint[Long, V] with
      override inline def test(value: Long): Boolean = value % doubleValue[V] == 0

    inline given [V <: NumConstant]: MultipleConstraint[Float, V] with
      override inline def test(value: Float): Boolean = value % doubleValue[V] == 0

    inline given [V <: NumConstant]: MultipleConstraint[Double, V] with
      override inline def test(value: Double): Boolean = value % doubleValue[V] == 0

    inline given [V <: Int | Long]: MultipleConstraint[BigInt, V] with

      override inline def test(value: BigInt): Boolean = value % BigInt(longValue[V]) == 0

    inline given[V <: NumConstant]: MultipleConstraint[BigDecimal, V] with

      override inline def test(value: BigDecimal): Boolean = value % BigDecimal(doubleValue[V]) == 0

    given [A, V1 <: A, V2 <: A](using V1 % V2 =:= Zero[A]): (Multiple[V1] ==> Multiple[V2]) = Implication()

  object Divide:
    private trait DivideConstraint[A, V <: NumConstant] extends Constraint[A, Divide[V]]:
      override inline def message: String = "Should divide " + stringValue[V]

    inline given [V <: NumConstant]: DivideConstraint[Int, V] with
      override inline def test(value: Int): Boolean = doubleValue[V] % value == 0

    inline given [V <: NumConstant]: DivideConstraint[Long, V] with
      override inline def test(value: Long): Boolean = doubleValue[V] % value == 0

    inline given [V <: NumConstant]: DivideConstraint[Float, V] with
      override inline def test(value: Float): Boolean = doubleValue[V] % value == 0

    inline given [V <: NumConstant]: DivideConstraint[Double, V] with
      override inline def test(value: Double): Boolean = doubleValue[V] % value == 0

    inline given [V <: Int | Long]: DivideConstraint[BigInt, V] with
      override inline def test(value: BigInt): Boolean = BigInt(longValue[V]) % value == 0

    inline given[V <: NumConstant]: DivideConstraint[BigDecimal, V] with
      override inline def test(value: BigDecimal): Boolean = BigDecimal(doubleValue[V]) % value == 0

  object NaN:
    private trait NaNConstraint[A] extends Constraint[A, NaN]:
      override inline def message: String = "Should be an unrepresentable number"

    inline given NaNConstraint[Float] with
      override inline def test(value: Float): Boolean = value.isNaN

    inline given NaNConstraint[Double] with
      override inline def test(value: Double): Boolean = value.isNaN

  object Infinity:
    private trait InfinityConstraint[A] extends Constraint[A, Infinity]:
      override inline def message: String = "Should be -infinity or +infinity"

    inline given InfinityConstraint[Float] with
      override inline def test(value: Float): Boolean = value.isInfinity

    inline given InfinityConstraint[Double] with
      override inline def test(value: Double): Boolean = value.isInfinity
