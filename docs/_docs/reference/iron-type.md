---
title: "Iron Type"
---

Refined types are represented in Iron by `IronType[A, C]` where:

- `A` is the base type
- `C` is the constraint (or "refinement") attached to `A`.

For example, `IronType[Int, Greater[0]]` represents an `Int` that should be greater than zero
(see [[Greater reference|io.github.iltotore.iron.constraint.numeric.Greater]]).

A more concise alias `:|` is often used instead: `Int :| Greater[0]` is equivalent to `IronType[Int, Greater[0]]`.
This alias is close to the
mathematical [predicate operator](https://en.wikipedia.org/wiki/Set-builder_notation#Sets_defined_by_a_predicate) `|`
used in set builders.

Refined types are subtypes of their unrefined form. For instance, `Int :| Greater[0]` is a subtype of `Int`.

```scala
val x: Int :| Greater[0] = ???
val y: Int = x //Compiles
```

`IronType[A, C]` is an opaque type alias of `A`. Therefore, a refined type desugars to its "raw" type, making refined
types overheadless.

```scala
val x: Int :| Greater[0] = ???
```

desugars to

```scala
val x: Int = ???
```