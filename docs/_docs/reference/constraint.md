---
title: "Constraint"
---

# Constraint

In Iron, a constraint consist of a type, called "dummy" or "proxy" type, associated with a given
instance of [[Constraint|io.github.iltotore.iron.Constraint]].

See [Refinement](refinement.md) for usage.

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
var x: Int :| Positive = 1
x = 0 //Compile-time error: Should be strictly positive
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
var x: Int :| Greater[5] = 6
x = 3 //Compile-time error: Should be greater than 5
```