---
title: "Constraint"
---

# Constraint

In Iron, a constraint consist of a type, called "dummy" or "proxy" type, associated with a given
instance of [[Constraint|io.github.iltotore.iron.Constraint]].

See [Refinement](refinement.md) for usage.

## Operations

Usually, you can make your constraint out of existing ones. Iron provides several operators to help you to compose them. 

### Union and intersection

Type union `C1 | C2` and intersection `C1 & C2` respectively act as a boolean OR/AND in Iron. For example, [[GreaterEqual|io.github.iltotore.iron.constraint.numeric.GreaterEqual]] is just a union of [[Greater|io.github.iltotore.iron.constraint.numeric.Greater]] and [[StrictEqual|io.github.iltotore.iron.constraint.numeric.StrictEqual]]:

```scala
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.{Greater, StrictEqual}

type GreaterEqual[V] = Greater[V] | StrictEqual[V]

val x: Int :| GreaterEqual[0] = 1 //OK
val y: Int :| GreaterEqual[0] = 1 //OK
val z: Int :| GreaterEqual[0] = -1 //Compile-time error: (Should be greater than 0 | Should strictly equal to 0)
```

Same goes for intersection:

```scala
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

```scala
final class Positive
```

The dummy type does nothing in itself. It is only used by the type system/implicit search to select the right
[[Constraint|io.github.iltotore.iron.Constraint]].

## Constraint implementation

Each refined type `A :| C` need an implicit instance of `Constraint[A, C]` to be verified. For instance,
`Int :| Positive` need a given instance of `Constraint[Int, Positive]`.

Here is how it looks:

```scala
given Constraint[Int, Positive] with

  override inline def test(value: Int): Boolean = value > 0

  override inline def message: String = "Should be strictly positive"
```

Note that you need to do this for each type. If your constraint supports multiple types (e.g numeric types),
you can use a trait to reduce boilerplate:

```scala
trait PositiveConstraint[A] extends Constraint[A, Positive]:
  override inline def message: String = "Should be strictly positive"

given PositiveConstraint[Int] with
  override inline def test(value: Int): Boolean = value > 0

given PositiveConstraint[Double] with
  override inline def test(value: Double): Boolean = value > 0.0
```

This constraint can now be used like any other:

```scala
val x: Int :| Positive = 1
val y: Int :| Positive = -1 //Compile-time error: Should be strictly positive
```

## Constraint parameters

You can parameterize your constraints.
Let's take the standard [[Greater constraint|io.github.iltotore.iron.constraint.numeric.Greater]].

Constraint parameters are held by the dummy type as type parameters, **not constructor parameters**.

```scala
final class Greater[V]
```

Then, we can get the value of the passed type using `scala.compiletime.constValue`:

```scala
given [V]: Constraint[Int, Greater[V]] with

  override inline def test(value: Int): Boolean = value > constValue[V]

  override inline def message: String = "Should be greater than " + stringValue[V]
```

Note that we're using [[stringValue|io.github.iltotore.iron.ops.stringValue]] in the `message` method to get
a __fully inlined__ String value of the given type because `String#toString` is not inlined.
This method is equivalent to `constValue[scala.compiletime.ops.any.ToString[V]]`.

Now testing the constraint:

```scala
val x: Int :| Greater[5] = 6
val y: Int :| Greater[5] = 3 //Compile-time error: Should be greater than 5
```