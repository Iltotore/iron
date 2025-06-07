---
title: "Iron 3.x migration"
---

# Migrating from Iron 2.x to 3.x

Some major API changes have been introduced in Iron 3.0.0 compared to Iron 2.x.
Most of them involved minor changes from a Iron 2.x codebase to 3.x.

## Scala 3.6.3 or more is required (Only for Iron <3.0.2)

Due to a bug in the compiler (which was completely blocking for Iron 3.0.0) that has been fixed in 3.6.3,
you need this version or higher for 3.x.

Iron's Scala version policy is to try to stick to the LTS as much as possible so one can expect Iron to
reach the next Scala LTS when it releases and jump from LTSs while avoiding intermediate Scala Next
versions in the future.

## Custom Constraint instances

The parameter `value` of method [[test|io.github.iltotore.iron.Constraint.test]] in
[[Constraint|io.github.iltotore.iron.Constraint]] is now `inline`.
If you made a custom `Constraint` instance such, for example:

```scala
given Constraint[Foo, Bar] with
  inline def test(value: Foo): Boolean = ???
  inline def message: String = ???
```

You need to make `value` inline:

```scala
given Constraint[Foo, Bar] with
  inline def test(inline value: Foo): Boolean = ???
  inline def message: String = ???
```

## Refined type definition

Many changes related to `RefinedTypeOps` definition have been introduced to provide better ergonomy.

In 2.x:

```scala
opaque type Temperature = Double :| Positive
object Temperature extends RefinedTypeOps[Double, Positive, Temperature]
```

In 3.x:

```scala
type Temperature = Temperature.T
object Temperature extends RefinedType[Double, Positive]
```

- `RefinedTypeOps` is now `RefinedType`
- All newtypes are opaque, you can no longer make transparent types

You also no longer need to duplicate the constraint type, therefore, the following pattern is obsolete:

```scala
type TemperatureR = DescribedAs[Positive, "Temperature should be positive"]
opaque type Temperature = Double :| TemperatureR
object Temperature extends RefinedTypeOps[Double, TemperatureR, Temperature]
```