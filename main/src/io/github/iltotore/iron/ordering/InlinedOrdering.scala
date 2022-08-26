package io.github.iltotore.iron.ordering

/**
 * An inline equivalent of scala.Ordering. Each method call is fully inlined at the point of use.
 *
 * @tparam T
 */
trait InlinedOrdering[T]:

  /**
   * Returns whether a comparison between `x` and `y` is defined, and if so
   * the result of `compare(x, y)`.
   */
  inline def tryCompare(inline x: T, inline y: T) = Some(compare(x, y))

  /**
   * Returns an integer whose sign communicates how x compares to y.
   *
   * The result sign has the following meaning:
   *
   *  - negative if x < y
   *  - positive if x > y
   *  - zero otherwise (if x == y)
   */
  transparent inline def compare(inline x: T, inline y: T): Int

  inline def compareAlias(inline x: T, inline y: T): Int = compare(x, y)

  /** Return true if `x` <= `y` in the ordering. */
  inline def lteq(inline x: T, inline y: T): Boolean = compare(x, y) <= 0

  /** Return true if `x` >= `y` in the ordering. */
  inline def gteq(inline x: T, inline y: T): Boolean = compare(x, y) >= 0

  /** Return true if `x` < `y` in the ordering. */
  inline def lt(inline x: T, inline y: T): Boolean = compare(x, y) < 0

  /** Return true if `x` > `y` in the ordering. */
  inline def gt(inline x: T, inline y: T): Boolean = compare(x, y) > 0

  /** Return true if `x` == `y` in the ordering. */
  inline def equiv(inline x: T, inline y: T): Boolean = compare(x, y) == 0

  /** Return `x` if `x` >= `y`, otherwise `y`. */
  inline def max[U <: T](inline x: U, inline y: U): U = inline if gteq(x, y) then x else y

  /** Return `x` if `x` <= `y`, otherwise `y`. */
  inline def min[U <: T](inline x: U, inline y: U): U = inline if lteq(x, y) then x else y
