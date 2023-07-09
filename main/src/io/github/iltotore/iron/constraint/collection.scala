package io.github.iltotore.iron.constraint

import io.github.iltotore.iron.{:|, ==>, Constraint, Implication}
import io.github.iltotore.iron.compileTime.*
import io.github.iltotore.iron.constraint.any.{DescribedAs, StrictEqual}
import io.github.iltotore.iron.constraint.numeric.{GreaterEqual, LessEqual}

import scala.compiletime.{constValue, summonInline}
import scala.compiletime.ops.string.Length
import scala.quoted.*

/**
 * Collection-related constraints.
 *
 * @see [[string]] which contains [[String]]-specific implementations for most of these constraints.
 */
object collection:

  /**
   * Tests if the length of the passed input satisfies the given constraint.
   * @tparam C the constraint to test on the given input.
   */
  final class Length[C]

  /**
   * Tests minimum length. Supports [[Iterable]] and [[String]] by default.
   *
   * @tparam V the minimum length of the tested input
   */
  type MinLength[V <: Int] = Length[GreaterEqual[V]] DescribedAs "Should have a minimum length of " + V

  /**
   * Tests maximum length. Supports [[Iterable]] and [[String]] by default.
   *
   * @tparam V the maximum length of the tested input
   */
  type MaxLength[V <: Int] = Length[LessEqual[V]] DescribedAs "Should have a maximum length of " + V

  /**
   * Tests exact length. Supports [[Iterable]] and [[String]] by default.
   */
  type FixedLength[V <: Int] = Length[StrictEqual[V]] DescribedAs "Should have an exact length of " + V

  /**
   * Tests if the input is empty.
   */
  type Empty = FixedLength[0] DescribedAs "Should be empty"

  /**
   * Tests if the given collection contains a specific value.
   *
   * @tparam V the value the input must contain.
   */
  final class Contain[V]

  /**
   * Tests if each element satisfies the given constraint.
   * @tparam C the constraint to test against each element.
   */
  final class ForAll[C]

  /**
   * Tests if each element except the last one satisfies the given constraint.
   *
   * @tparam C the constraint to test against the init.
   */
  final class Init[C]

  /**
   * Tests if each element except the first one satisfies the given constraint.
   * @tparam C the constraint to test against the tail.
   */
  final class Tail[C]

  /**
   * Tests if at least one element satisfies the given constraint.
   *
   * @tparam C the constraint to test against each element.
   */
  final class Exists[C]

  /**
   * Tests if the head of the passed input satisfies the given constraint.
   * @tparam C the constraint to test against the first element (head).
   */
  final class Head[C]

  /**
   * Tests if the last element of the passed input satisfies the given constraint.
   * @tparam C the constraint to test against the last element.
   */
  final class Last[C]

  object Length:

    class LengthIterable[I <: Iterable[?], C, Impl <: Constraint[Int, C]](using Impl) extends Constraint[I, Length[C]]:

      override inline def test(value: I): Boolean = summonInline[Impl].test(value.size)

      override inline def message: String = "Length: (" + summonInline[Impl].message + ")"

    inline given [I <: Iterable[?], C, Impl <: Constraint[Int, C]](using inline impl: Impl): LengthIterable[I, C, Impl] =
      new LengthIterable

    class LengthString[C, Impl <: Constraint[Int, C]](using Impl) extends Constraint[String, Length[C]]:

      override inline def test(value: String): Boolean = ${ checkString('value, '{ summonInline[Impl] }) }

      override inline def message: String = "Length: (" + summonInline[Impl].message + ")"

    inline given lengthString[C, Impl <: Constraint[Int, C]](using inline impl: Impl): LengthString[C, Impl] = new LengthString

    private def checkString[C, Impl <: Constraint[Int, C]](expr: Expr[String], constraintExpr: Expr[Impl])(using Quotes): Expr[Boolean] =

      import quotes.reflect.*

      expr.value match
        case Some(value) => applyConstraint(Expr(value.length), constraintExpr)

        case None => applyConstraint('{ $expr.length }, constraintExpr)

    given [C1, C2](using C1 ==> C2): (Length[C1] ==> Length[C2]) = Implication()

  object Contain:
    inline given [A, V <: A, I <: Iterable[A]]: Constraint[I, Contain[V]] with

      override inline def test(value: I): Boolean = value.iterator.contains(constValue[V])

      override inline def message: String = "Should contain at most " + stringValue[V] + " elements"

    inline given [V <: String]: Constraint[String, Contain[V]] with

      override inline def test(value: String): Boolean = ${ checkString('value, '{ constValue[V] }) }

      override inline def message: String = "Should contain the string " + constValue[V]

    private def checkString(expr: Expr[String], partExpr: Expr[String])(using Quotes): Expr[Boolean] =
      (expr.value, partExpr.value) match
        case (Some(value), Some(part)) => Expr(value.contains(part))
        case _                         => '{ ${ expr }.contains($partExpr) }

  object ForAll:

    class ForAllIterable[A, I <: Iterable[A], C, Impl <: Constraint[A, C]](using Impl) extends Constraint[I, ForAll[C]]:

      override inline def test(value: I): Boolean = value.forall(summonInline[Impl].test(_))

      override inline def message: String = "For each element: (" + summonInline[Impl].message + ")"

    inline given [A, I <: Iterable[A], C, Impl <: Constraint[A, C]](using inline impl: Impl): ForAllIterable[A, I, C, Impl] =
      new ForAllIterable

    class ForAllString[C, Impl <: Constraint[Char, C]](using Impl) extends Constraint[String, ForAll[C]]:

      override inline def test(value: String): Boolean = ${ checkString('value, '{ summonInline[Impl] }) }

      override inline def message: String = "For each element: (" + summonInline[Impl].message + ")"

    inline given forAllString[C, Impl <: Constraint[Char, C]](using inline impl: Impl): ForAllString[C, Impl] = new ForAllString

    private def checkString[C, Impl <: Constraint[Char, C]](expr: Expr[String], constraintExpr: Expr[Impl])(using Quotes): Expr[Boolean] =

      import quotes.reflect.*

      expr.value match
        case Some(value) =>
          value
            .map(Expr.apply)
            .map(applyConstraint(_, constraintExpr))
            .foldLeft(Expr(true))((e, t) => '{ $e && $t })

        case None => '{ $expr.forallOptimized(c => ${ applyConstraint('c, constraintExpr) }) }

    given [C1, C2](using C1 ==> C2): (ForAll[C1] ==> Exists[C2]) = Implication()
    given [C1, C2](using C1 ==> C2): (ForAll[C1] ==> Last[C2]) = Implication()
    given [C1, C2](using C1 ==> C2): (ForAll[C1] ==> Init[C2]) = Implication()
    given [C1, C2](using C1 ==> C2): (ForAll[C1] ==> Tail[C2]) = Implication()

  object Init:

    class InitIterable[A, I <: Iterable[A], C, Impl <: Constraint[A, C]](using Impl) extends Constraint[I, Init[C]]:

      override inline def test(value: I): Boolean = value.isEmpty || value.init.forall(summonInline[Impl].test(_))

      override inline def message: String = "For each element except head: (" + summonInline[Impl].message + ")"

    inline given [A, I <: Iterable[A], C, Impl <: Constraint[A, C]](using inline impl: Impl): InitIterable[A, I, C, Impl] =
      new InitIterable

    class InitString[C, Impl <: Constraint[Char, C]](using Impl) extends Constraint[String, Init[C]]:

      override inline def test(value: String): Boolean = ${ checkString('value, '{ summonInline[Impl] }) }

      override inline def message: String = "For each element except last: (" + summonInline[Impl].message + ")"

    inline given initString[C, Impl <: Constraint[Char, C]](using inline impl: Impl): InitString[C, Impl] = new InitString

    private def checkString[C, Impl <: Constraint[Char, C]](expr: Expr[String], constraintExpr: Expr[Impl])(using Quotes): Expr[Boolean] =

      import quotes.reflect.*

      expr.value match
        case Some(value) =>
          value
            .init
            .map(Expr.apply)
            .map(applyConstraint(_, constraintExpr))
            .foldLeft(Expr(true))((e, t) => '{ $e && $t })

        case None => '{ $expr.init.forallOptimized(c => ${ applyConstraint('c, constraintExpr) }) }

    given [C1, C2](using C1 ==> C2): (Init[C1] ==> Exists[C2]) = Implication()

    given [C1, C2](using C1 ==> C2): (Init[C1] ==> Head[C2]) = Implication()

  object Tail:

    class TailIterable[A, I <: Iterable[A], C, Impl <: Constraint[A, C]](using Impl) extends Constraint[I, Tail[C]]:

      override inline def test(value: I): Boolean = value.isEmpty || value.tail.forall(summonInline[Impl].test(_))

      override inline def message: String = "For each element: (" + summonInline[Impl].message + ")"

    inline given [A, I <: Iterable[A], C, Impl <: Constraint[A, C]](using inline impl: Impl): TailIterable[A, I, C, Impl] =
      new TailIterable

    class TailString[C, Impl <: Constraint[Char, C]](using Impl) extends Constraint[String, Tail[C]]:

      override inline def test(value: String): Boolean = ${ checkString('value, '{ summonInline[Impl] }) }

      override inline def message: String = "For each element: (" + summonInline[Impl].message + ")"

    inline given tailString[C, Impl <: Constraint[Char, C]](using inline impl: Impl): TailString[C, Impl] = new TailString

    private def checkString[C, Impl <: Constraint[Char, C]](expr: Expr[String], constraintExpr: Expr[Impl])(using Quotes): Expr[Boolean] =

      import quotes.reflect.*

      expr.value match
        case Some(value) =>
          value
            .tail
            .map(Expr.apply)
            .map(applyConstraint(_, constraintExpr))
            .foldLeft(Expr(true))((e, t) => '{ $e && $t })

        case None => '{ $expr.tail.forallOptimized(c => ${ applyConstraint('c, constraintExpr) }) }

    given [C1, C2](using C1 ==> C2): (Tail[C1] ==> Exists[C2]) = Implication()
    given [C1, C2](using C1 ==> C2): (Tail[C1] ==> Last[C2]) = Implication()

  object Exists:

    class ExistsIterable[A, I <: Iterable[A], C, Impl <: Constraint[A, C]](using Impl) extends Constraint[I, Exists[C]]:

      override inline def test(value: I): Boolean = value.exists(summonInline[Impl].test(_))

      override inline def message: String = "At least one: (" + summonInline[Impl].message + ")"

    inline given [A, I <: Iterable[A], C, Impl <: Constraint[A, C]](using inline impl: Impl): ExistsIterable[A, I, C, Impl] =
      new ExistsIterable

    class ExistsString[C, Impl <: Constraint[Char, C]](using Impl) extends Constraint[String, Exists[C]]:

      override inline def test(value: String): Boolean = ${ checkString('value, '{ summonInline[Impl] }) }

      override inline def message: String = "At least one element: (" + summonInline[Impl].message + ")"

    inline given existsString[C, Impl <: Constraint[Char, C]](using inline impl: Impl): ExistsString[C, Impl] = new ExistsString

    private def checkString[C, Impl <: Constraint[Char, C]](expr: Expr[String], constraintExpr: Expr[Impl])(using Quotes): Expr[Boolean] =

      import quotes.reflect.*

      expr.value match
        case Some(value) =>
          value
            .map(Expr.apply)
            .map(applyConstraint(_, constraintExpr))
            .foldLeft(Expr(false))((e, t) => '{ $e || $t })

        case None => '{ $expr.existsOptimized(c => ${ applyConstraint('c, constraintExpr) }) }

  object Head:

    class HeadIterable[A, I <: Iterable[A], C, Impl <: Constraint[A, C]](using Impl) extends Constraint[I, Head[C]]:

      override inline def test(value: I): Boolean = value.headOption.exists(summonInline[Impl].test(_))

      override inline def message: String = "Head: (" + summonInline[Impl].message + ")"

    inline given [A, I <: Iterable[A], C, Impl <: Constraint[A, C]](using inline impl: Impl): HeadIterable[A, I, C, Impl] =
      new HeadIterable

    class HeadString[C, Impl <: Constraint[Char, C]](using Impl) extends Constraint[String, Head[C]]:

      override inline def test(value: String): Boolean = ${ checkString('value, '{ summonInline[Impl] }) }

      override inline def message: String = "Head: (" + summonInline[Impl].message + ")"

    inline given headString[C, Impl <: Constraint[Char, C]](using inline impl: Impl): HeadString[C, Impl] = new HeadString

    private def checkString[C, Impl <: Constraint[Char, C]](expr: Expr[String], constraintExpr: Expr[Impl])(using Quotes): Expr[Boolean] =
      expr.value match
        case Some(value) =>
          value.headOption match
            case Some(head) => applyConstraint(Expr(head), constraintExpr)
            case None       => Expr(false)
        case None => '{ $expr.headOption.exists(head => ${ applyConstraint('{ head }, constraintExpr) }) }

    given [C1, C2](using C1 ==> C2): (Head[C1] ==> Exists[C2]) = Implication()

  object Last:

    class LastIterable[A, I <: Iterable[A], C, Impl <: Constraint[A, C]](using Impl) extends Constraint[I, Last[C]]:

      override inline def test(value: I): Boolean = value.lastOption.exists(summonInline[Impl].test(_))

      override inline def message: String = "Last: (" + summonInline[Impl].message + ")"

    inline given [A, I <: Iterable[A], C, Impl <: Constraint[A, C]](using inline impl: Impl): LastIterable[A, I, C, Impl] =
      new LastIterable

    class LastString[C, Impl <: Constraint[Char, C]](using Impl) extends Constraint[String, Last[C]]:

      override inline def test(value: String): Boolean = ${ checkString('value, '{ summonInline[Impl] }) }

      override inline def message: String = "Last: (" + summonInline[Impl].message + ")"

    inline given lastString[C, Impl <: Constraint[Char, C]](using inline impl: Impl): LastString[C, Impl] = new LastString

    private def checkString[C, Impl <: Constraint[Char, C]](expr: Expr[String], constraintExpr: Expr[Impl])(using Quotes): Expr[Boolean] =
      expr.value match
        case Some(value) =>
          value.lastOption match
            case Some(last) => applyConstraint(Expr(last), constraintExpr)
            case None       => Expr(false)
        case None => '{ $expr.lastOption.exists(last => ${ applyConstraint('{ last }, constraintExpr) }) }

    given [C1, C2](using C1 ==> C2): (Last[C1] ==> Exists[C2]) = Implication()

  /**
   * Scala's [[Function1]] doesn't have a specialization on [[Char]] arguments, which causes each char in the string to be boxed
   * when calling `forall`. This trait is used as a substitute to avoid this issue.
   */
  private trait EvalChar:
    def apply(value: Char): Boolean

  extension (s: String)
    private def forallOptimized(p: EvalChar): Boolean =
      var i = 0
      val len = s.length
      while i < len do
        if !p(s.charAt(i)) then return false
        i += 1
      true

    private def existsOptimized(p: EvalChar): Boolean =
      var i = 0
      val len = s.length
      while i < len do
        if p(s.charAt(i)) then return true
        i += 1
      false
