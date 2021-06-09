package io.github.iltotore.iron.numeric

/**
 * An inline equivalent of scala.Ordering. Each method call is fully inlined at the point of use.
 * @tparam T
 */
trait InlineOrdering[T] { outer =>

  /** Returns whether a comparison between `x` and `y` is defined, and if so
   * the result of `compare(x, y)`.
   */
  inline def tryCompare(inline x: T, inline y: T) = Some(compare(x, y))

  /** Returns an integer whose sign communicates how x compares to y.
   *
   * The result sign has the following meaning:
   *
   *  - negative if x < y
   *  - positive if x > y
   *  - zero otherwise (if x == y)
   */
  inline def compare(inline x: T, inline y: T): Int

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
  inline def max[U <: T](inline x: U, inline y: U): U = inline if (gteq(x, y)) x else y

  /** Return `x` if `x` <= `y`, otherwise `y`. */
  inline def min[U <: T](inline x: U, inline y: U): U = inline if (lteq(x, y)) x else y

  /** Given f, a function from U into T, creates an Ordering[U] whose compare
   * function is equivalent to:
   *
   * {{{
   * def compare(x:U, y:U) = Ordering[T].compare(f(x), f(y))
   * }}}
   */
  inline def on[U](inline f: U => T): Ordering[U] = new Ordering[U] {
    def compare(x: U, y: U) = outer.compare(f(x), f(y))
  }

  /** Creates an Ordering[T] whose compare function returns the
   * result of this Ordering's compare function, if it is non-zero,
   * or else the result of `other`s compare function.
   *
   * @example
   * {{{
   * case class Pair(a: Int, b: Int)
   *
   * val pairOrdering = Ordering.by[Pair, Int](_.a)
   *                            .orElse(Ordering.by[Pair, Int](_.b))
   * }}}
   * @param other an Ordering to use if this Ordering returns zero
   */
  inline def orElse(inline other: Ordering[T]): Ordering[T] = (x, y) => {
    val res1 = outer.compare(x, y)
    inline if (res1 != 0) res1 else other.compare(x, y)
  }

  /** Given f, a function from T into S, creates an Ordering[T] whose compare
   * function returns the result of this Ordering's compare function,
   * if it is non-zero, or else a result equivalent to:
   *
   * {{{
   * Ordering[S].compare(f(x), f(y))
   * }}}
   *
   * This function is equivalent to passing the result of `Ordering.by(f)`
   * to `orElse`.
   *
   * @example
   * {{{
   * case class Pair(a: Int, b: Int)
   *
   * val pairOrdering = Ordering.by[Pair, Int](_.a)
   *                            .orElseBy[Int](_.b)
   * }}}
   */
  inline def orElseBy[S](inline f: T => S)(implicit inline ord: Ordering[S]): Ordering[T] = (x, y) => {
    val res1 = outer.compare(x, y)
    if (res1 != 0) res1 else ord.compare(f(x), f(y))
  }
}

object InlineOrdering {

  /**
   * Create an Ordering[T] instance from an InlineOrdering to reduce boilerplate.
   */
  given [T](using inlined: InlineOrdering[T]): Ordering[T] with {
    override inline def compare(x: T, y: T): Int = inlined.compareAlias(x, y)
  }
}