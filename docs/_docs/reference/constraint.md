---
title: "Constraint"
---

# Constraint

In Iron, a constraint consist of a type, called "dummy" type, associated with a given
instance of [[Constraint|io.github.iltotore.iron.Constraint]].

See [Refinement](refinement.md) for usage.

## Operations

Usually, you can make your constraint out of existing ones. Iron provides several operators to help you to compose them. 

### Union and intersection

Type union `C1 | C2` and intersection `C1 & C2` respectively act as a boolean OR/AND in Iron. For example, [[GreaterEqual|io.github.iltotore.iron.constraint.numeric.GreaterEqual]] is just a union of [[Greater|io.github.iltotore.iron.constraint.numeric.Greater]] and [[StrictEqual|io.github.iltotore.iron.constraint.any.StrictEqual]]:

```scala sc-name:GreaterEqual.scala
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

type GreaterEqual[V] = Greater[V] | StrictEqual[V]
```
```scala  sc-compile-with:GreaterEqual.scala
val x: Int :| GreaterEqual[0] = 1 //OK
val y: Int :| GreaterEqual[0] = -1 //Compile-time error: (Should be greater than 0 | Should strictly equal to 0)
```

Same goes for intersection:

```scala sc-compile-with:GreaterEqual.scala
type Between[Min, Max] = GreaterEqual[Min] & LessEqual[Max]
```

### Other operations

Most constraint operators provided by Iron are "normal" constraints taking another constraint as parameter.

Here is a list of the most used operators:
- [[Not\[C\]|io.github.iltotore.iron.constraint.any.Not]]: like a boolean "not". Negate the result of the `C` constraint.
- [[DescribedAs\[C, V\]|io.github.iltotore.iron.constraint.any.DescribedAs]]: attach a custom description `V` to `C`.
- [[ForAll\[C\]|io.github.iltotore.iron.constraint.collection.ForAll]]: check if the `C` constraint passes for all elements of a collection/String
- [[Exists\[C\]|io.github.iltotore.iron.constraint.collection.Exists]]: check if the `C` constraint passes for at least one element of a collection/String

## Dummy type

Usually, the dummy type is represented by a final class. Note that this class (or whatever entity you choose as a dummy)
should not have constructor parameters.

```scala sc-name:Positive.scala
final class Positive
```

The dummy type does nothing in itself. It is only used by the type system/implicit search to select the right
[[Constraint|io.github.iltotore.iron.Constraint]].

## Constraint implementation

Each refined type `A :| C` need an implicit instance of `Constraint[A, C]` to be verified. For instance,
`Int :| Positive` need a given instance of `Constraint[Int, Positive]`.

Here is how it looks:

```scala  sc-name:PositiveAndConstraint.scala sc-compile-with:Positive.scala
//{
import io.github.iltotore.iron.*

//}
given Constraint[Int, Positive] with

  override inline def test(value: Int): Boolean = value > 0

  override inline def message: String = "Should be strictly positive"
```

Note that you need to do this for each type. If your constraint supports multiple types (e.g numeric types),
you can use a trait to reduce boilerplate:

```scala  sc-compile-with:Positive.scala
//{
import io.github.iltotore.iron.*

//}
trait PositiveConstraint[A] extends Constraint[A, Positive]:
  override inline def message: String = "Should be strictly positive"

given PositiveConstraint[Int] with
  override inline def test(value: Int): Boolean = value > 0

given PositiveConstraint[Double] with
  override inline def test(value: Double): Boolean = value > 0.0
```

This constraint can now be used like any other:

```scala  sc-compile-with:PositiveAndConstraint.scala
val x: Int :| Positive = 1
val y: Int :| Positive = -1 //Compile-time error: Should be strictly positive
```

## Constraint parameters

You can parameterize your constraints.
Let's take the standard [[Greater constraint|io.github.iltotore.iron.constraint.numeric.Greater]].

Constraint parameters are held by the dummy type as type parameters, **not constructor parameters**.

```scala sc-name:Greater.scala
final class Greater[V]
```

Then, we can get the value of the passed type using `scala.compiletime.constValue`:

```scala sc-name:GreaterAndConstraint.scala sc-compile-with:Greater.scala
//{
import io.github.iltotore.iron.*
//}
import scala.compiletime.constValue

given [V]: Constraint[Int, Greater[V]] with

  override inline def test(value: Int): Boolean = value > constValue[V]

  override inline def message: String = "Should be greater than " + stringValue[V]
```

Note that we're using [[stringValue|io.github.iltotore.iron.ops.stringValue]] in the `message` method to get
a __fully inlined__ String value of the given type because `String#toString` is not inlined.
This method is equivalent to `constValue[scala.compiletime.ops.any.ToString[V]]`.

Now testing the constraint:

```scala sc-compile-with:GreaterAndConstraint.scala
val x: Int :| Greater[5] = 6
val y: Int :| Greater[5] = 3 //Compile-time error: Should be greater than 5
```

## Runtime proxy

Iron provides a proxy for `Constraint`, named `RuntimeConstraint`. It is used the same way as `Constraint`:

```scala
def refineRuntimeOption[A, C](value: A)(using constraint: RuntimeConstraint[A, C]): Option[A :| C] =
  Option.when(constraint.test(value))(value.asInstanceOf[A :| C])

refineRuntimeOption[Int, Positive](5) //Some(5)
refineRuntimeOption[Int, Positive](-5) //None
```

with two advantages:
- It does not need the summoning method (here `refineOption`) to be `inline`
- It significantly lowers the generated bytecode and usually improves performances

Therefore, it is recommended to use `RuntimeConstraint` instead of `Constraint` when using the instance at runtime.
For example, most of `RefinedTypeOps`'s (see [New types](newtypes.md#runtime-refinement)) methods use a
`RuntimeConstraint`.

It is also recommended to use `RuntimeConstraint` to derive typeclasses, especially when using a `given` with a function
value.

```scala
trait FromString[A]:

  def fromString(text: String): Either[String, A]

given [A, C](using constraint: RuntimeConstraint[A, C], instanceA: FromString[A]): FromString[A :| C] = text =>
  instanceA
    .fromString(text)
    .filterOrElse(constraint.test(_), constraint.message)
    .map(_.asInstanceOf[A :| C])
```

Note that using a `Constraint` here (and having to our given instance `inline`) will produce a warning:

> An inline given alias with a function value as right-hand side can significantly increase
generated code size. You should either drop the `inline` or rewrite the given with an
explicit `apply` method.

`RuntimeConstraint` is also useful when you need to reuse the same constraint. Here is an example from `RefinedTypeOps`:

```scala
trait RefinedTypeOps[A, C, T]:

  inline def rtc: RuntimeConstraint[A, C] = ???

  def option(value: A): Option[T] =
    Option.when(rtc.test(value))(value.asInstanceOf[T])
```

# Pre-defined constraints

Iron provides a set of pre-defined constraints in the `io.github.iltotore.iron.constraint` package.
You can find them in the [API documentation](https://iltotore.github.io/iron/api/io/github/iltotore/iron/constraint/index.html).

## Global constraints

Some constraints are available for all types.
They are located in the `io.github.iltotore.iron.constraint.any` object.

Here is a list of the most used ones:

- [[StrictEqual|io.github.iltotore.iron.constraint.any.StrictEqual]]: check if a value is equal to a given one.
- [[Not|io.github.iltotore.iron.constraint.any.Not]]: a constraint decorator to negate another constraint.
The [[!|io.github.iltotore.iron.constraint.any.!]] alias is also available.
- [[DescribedAs|io.github.iltotore.iron.constraint.any.DescribedAs]]: attach a custom description to a constraint.
- [[True|io.github.iltotore.iron.constraint.any.True]]: an always-true constraint.
- [[False|io.github.iltotore.iron.constraint.any.False]]: an always-false constraint.
- [[Xor|io.github.iltotore.iron.constraint.any.Xor]]: a boolean XOR between two constraints.
- [[In|io.github.iltotore.iron.constraint.any.In]]: check if a value is in a given value tuple.

## Char constraints

Some constraints are available for `Char` types.
They are located in the `io.github.iltotore.iron.constraint.char` object.

Here is a list of the most used ones:

- [[Digit|io.github.iltotore.iron.constraint.char.Digit]]: check if a character is a digit.
- [[Letter|io.github.iltotore.iron.constraint.char.Letter]]: check if a character is a letter.
- [[LowerCase|io.github.iltotore.iron.constraint.char.LowerCase]]: check if a character is a lower case character.
- [[UpperCase|io.github.iltotore.iron.constraint.char.UpperCase]]: check if a character is an upper case character.
- [[Whitespace|io.github.iltotore.iron.constraint.char.Whitespace]]: check if a character is a whitespace character.
- [[Special|io.github.iltotore.iron.constraint.char.Special]]: check if a character is a special character (i.e. not a digit nor a letter).

## Numeric constraints

Some constraints are available for numeric types.
They are located in the `io.github.iltotore.iron.constraint.numeric` object.

Here is a list of the most used ones:

- [[Less|io.github.iltotore.iron.constraint.numeric.Less]]: check if a value is less than a given one.
- [[Greater|io.github.iltotore.iron.constraint.numeric.Greater]]: check if a value is greater than a given one.
- [[LessEqual|io.github.iltotore.iron.constraint.numeric.LessEqual]]: check if a value is less than or equal to a given one.
- [[GreaterEqual|io.github.iltotore.iron.constraint.numeric.GreaterEqual]]: check if a value is greater than or equal to a given one.
- [[Positive|io.github.iltotore.iron.constraint.numeric.Positive]]: check if a value is strictly positive.
- [[Negative|io.github.iltotore.iron.constraint.numeric.Negative]]: check if a value is strictly negative.
- [[Positive0|io.github.iltotore.iron.constraint.numeric.Positive0]]: check if a value is positive or zero.
- [[Negative0|io.github.iltotore.iron.constraint.numeric.Negative0]]: check if a value is negative or zero.
- [[Interval.Closed|io.github.iltotore.iron.constraint.numeric.Interval.Closed]]: check if a value is in a closed interval.
- [[Interval.Open|io.github.iltotore.iron.constraint.numeric.Interval.Open]]: check if a value is in an open interval.
- [[Interval.OpenClosed|io.github.iltotore.iron.constraint.numeric.Interval.OpenClosed]]: check if a value is in an open-closed interval.
- [[Interval.ClosedOpen|io.github.iltotore.iron.constraint.numeric.Interval.ClosedOpen]]: check if a value is in a closed-open interval.
- [[Infinity|io.github.iltotore.iron.constraint.numeric.Infinity]]: check if a value is infinite (positive or negative).
- [[NaN|io.github.iltotore.iron.constraint.numeric.NaN]]: check if a value is not a representable number.
- [[Multiple|io.github.iltotore.iron.constraint.numeric.Multiple]]: check if a value is a multiple of another one.
- [[Divide|io.github.iltotore.iron.constraint.numeric.Divide]]: check if a value is a divisor of another one.
- [[Odd|io.github.iltotore.iron.constraint.numeric.Odd]]: check if a value is odd.
- [[Even|io.github.iltotore.iron.constraint.numeric.Even]]: check if a value is even.

## Collection constraints

Some constraints are available for collections.
They are located in the `io.github.iltotore.iron.constraint.collection` object.

Here is a list of the most used ones:

- [[ForAll|io.github.iltotore.iron.constraint.collection.ForAll]]: check if a constraint passes for all elements of a collection.
- [[Exists|io.github.iltotore.iron.constraint.collection.Exists]]: check if a constraint passes for at least one element of a collection.
- [[Length|io.github.iltotore.iron.constraint.collection.Length]]: check if the collection length satisfies a given constraint.
- [[Empty|io.github.iltotore.iron.constraint.collection.Empty]]: check if a collection is empty.
- [[FixedLength|io.github.iltotore.iron.constraint.collection.FixedLength]]: check if a collection has a fixed length.
- [[MinLength|io.github.iltotore.iron.constraint.collection.MinLength]]: check if a collection has a minimum length.
- [[MaxLength|io.github.iltotore.iron.constraint.collection.MaxLength]]: check if a collection has a maximum length.
- [[Contains|io.github.iltotore.iron.constraint.collection.Contains]]: check if a collection contains a given element.
- [[Head|io.github.iltotore.iron.constraint.collection.Head]]: check if a collection's head satisfies a given constraint.
- [[Last|io.github.iltotore.iron.constraint.collection.Last]]: check if a collection's last element satisfies a given constraint.
- [[Tail|io.github.iltotore.iron.constraint.collection.Tail]]: check if a collection's tail satisfies a given constraint.
- [[Init|io.github.iltotore.iron.constraint.collection.Init]]: check if a collection's init satisfies a given constraint.

## String constraints

Some constraints are available for `String` types.
They are located in the `io.github.iltotore.iron.constraint.string` object.
Note that, as `String` is an `Iterable[Char]`, you can use the collection constraints on `String`.

Here is a list of the most used ones:

- [[Blank|io.github.iltotore.iron.constraint.string.Blank]]: check if a string is blank (i.e. empty or only containing whitespaces).
- [[StartWith|io.github.iltotore.iron.constraint.string.StartWith]]: check if a string starts with a given prefix.
- [[EndWith|io.github.iltotore.iron.constraint.string.EndWith]]: check if a string ends with a given suffix.
- [[Match|io.github.iltotore.iron.constraint.string.Match]]: check if a string matches a given regular expression.
- [[Alphanumeric|io.github.iltotore.iron.constraint.string.Alphanumeric]]: check if a string contains only alphanumeric characters.
- [[LettersLowerCase|io.github.iltotore.iron.constraint.string.LettersLowerCase]]: check if all letters of a string are lower-cased letters.
- [[LettersUpperCase|io.github.iltotore.iron.constraint.string.LettersUpperCase]]: check if all letters of a string are upper-cased letters.
- [[Trimmed|io.github.iltotore.iron.constraint.string.Trimmed]]: check if a string is trimmed (i.e. without leading and trailing whitespaces).
- [[ValidUUID|io.github.iltotore.iron.constraint.string.ValidUUID]]: check if a string is a valid UUID.
- [[ValidURL|io.github.iltotore.iron.constraint.string.ValidURL]]: check if a string is a valid URL.
- [[SemanticVersion|io.github.iltotore.iron.constraint.string.SemanticVersion]]: check if a string is a valid semantic version as defined on [semver.org](https://semver.org/#is-there-a-suggested-regular-expression-regex-to-check-a-semver-string).
